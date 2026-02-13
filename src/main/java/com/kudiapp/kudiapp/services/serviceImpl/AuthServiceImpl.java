package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.config.security.UserDetailsImpl;
import com.kudiapp.kudiapp.config.security.jwt.JwtUtils;
import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.authDTOS.*;
import com.kudiapp.kudiapp.dto.response.LoginResponseDto;
import com.kudiapp.kudiapp.dto.token.RefreshTokenRequest;
import com.kudiapp.kudiapp.dto.token.TokenRefreshResponse;
import com.kudiapp.kudiapp.exceptions.*;
import com.kudiapp.kudiapp.models.NewsLetter;
import com.kudiapp.kudiapp.models.RefreshToken;
import com.kudiapp.kudiapp.models.User;
import com.kudiapp.kudiapp.models.approles.Role;
import com.kudiapp.kudiapp.repository.NewsLetterRepository;
import com.kudiapp.kudiapp.repository.RoleRepository;
import com.kudiapp.kudiapp.repository.UserRepository;
import com.kudiapp.kudiapp.services.AuthService;
import com.kudiapp.kudiapp.services.EmailService;
import com.kudiapp.kudiapp.services.RefreshTokenService;
import com.kudiapp.kudiapp.services.VerificationCodeService;
import com.kudiapp.kudiapp.utills.SecurityUtil;
import com.kudiapp.kudiapp.utills.UserIdUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final EmailService  emailService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final NewsLetterRepository newsLetterRepository;
    private final SecurityUtil securityUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserRepository userRepository, VerificationCodeService verificationCodeService, EmailService emailService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, NewsLetterRepository newsLetterRepository, SecurityUtil securityUtil, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.newsLetterRepository = newsLetterRepository;
        this.securityUtil = securityUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public GenericResponse login(LoginRequest loginRequestDto) {
        validateLoginRequest(loginRequestDto);

        User userEntity = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userEntity.isEnable2Fa()) {
            sendOtpAndNotify(userEntity);
            return createOtpSentResponse();
        }

        if (isInvalidPassword(loginRequestDto.getPassword(), userEntity)) {
            handleFailedLoginAttempt(userEntity);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        Authentication authentication = authenticateUser(loginRequestDto);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = generateAccessToken(authentication);
        RefreshToken savedRefreshToken = refreshTokenService.createRefreshToken(userEntity.getId());

        List<String> roles = extractRoles(userDetails);

        // Update user last login time
        updateUserLastLogin(userEntity);

        // Prepare response DTO
        LoginResponseDto responseDto = buildLoginResponseDto(userEntity, accessToken, savedRefreshToken, roles);

        return buildLoginSuccessResponse(responseDto);
    }

    @Override
    @Transactional
    public GenericResponse register(RegisterRequest registerRequest) {
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new InvalidCredentialsException("Provide valid email...");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = createUserTemplate(registerRequest);
        User savedUser = userRepository.save(user);
        verificationCodeService.sendOtpCode(savedUser.getEmail());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Account created successfully... Check and verify your email")
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    @Override
    @Transactional
    public GenericResponse makeAdmin(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidCredentialsException("Provide valid email...");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found for provided email: " + email)
                );

        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() ->
                        new RoleNotFoundException("ROLE_ADMIN not found in database")
                );

        // ✅ Check if already admin
        boolean isAlreadyAdmin = user.getRoles()
                .stream()
                .anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"));

        if (isAlreadyAdmin) {
            return GenericResponse.builder()
                    .isSuccess(false)
                    .message("User is already an admin")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }

        user.getRoles().add(adminRole);
        userRepository.save(user);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("User is now an admin")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponse getAllContactUs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NewsLetter> contactMessages = newsLetterRepository.findAll(pageable);

        return new GenericResponse("Contact messages fetched successfully", HttpStatus.OK, contactMessages);
    }

    @Override
    public GenericResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtUtils.generateTokenFromUsername(user.getEmail());

                    TokenRefreshResponse tokenResponse = new TokenRefreshResponse(newAccessToken, requestRefreshToken);

                    return GenericResponse.builder()
                            .isSuccess(true)
                            .message("Token refreshed successfully")
                            .data(tokenResponse)
                            .httpStatus(HttpStatus.OK)
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not valid!"));
    }

    @Override
    public GenericResponse contactUs(ContactUsRequest contactUsRequest) {

        NewsLetter newsLetter = NewsLetter.builder()
                .email(contactUsRequest.getEmail())
                .name(contactUsRequest.getName())
                .message(contactUsRequest.getMessage())
                .phoneNumber(contactUsRequest.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        newsLetterRepository.save(newsLetter);
        return GenericResponse.builder()
                .isSuccess(true)
                .message("Message sent successfully. Stay tuned for response from our ADMIN via mail.")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponse contactUsAttendedTo(Long contactusId) {
        Optional<NewsLetter> optionalNewsLetter = newsLetterRepository.findById(contactusId);
        if (optionalNewsLetter.isEmpty()) {
            throw new ResourceNotFoundException("Contact us message not found with id: " + contactusId);
        }
        NewsLetter newsLetter = optionalNewsLetter.get();
        newsLetter.setAttendedTo(!newsLetter.isAttendedTo());
        newsLetter.setUpdatedAt(LocalDateTime.now());
        newsLetter.setAttendedToBy(securityUtil.getCurrentUserId());
        newsLetterRepository.save(newsLetter);
        return GenericResponse.builder().httpStatus(HttpStatus.OK).build();
    }

    @Override
    public GenericResponse verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email).orElseThrow( () -> new UserNotFoundException("User not found for provided email:" + email));
        if (user.isVerified()) {
            throw new UserAlreadyExistsException("User already verified");
        }
        boolean emailVerified = verificationCodeService.verifyOtpCode(email, code);
        if (!emailVerified) {
            throw new InvalidTokenException("Email verification failed...");
        }
        user.setVerified(true);
        user.setVerificationDate(LocalDateTime.now());
        user.setEnabled(true);
        userRepository.save(user);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Email verified successfully.")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponse resendOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow( () -> new UserNotFoundException("User not found for provided email:" + email));
        if (user.isVerified()) {
            throw new UserAlreadyExistsException("User already verified");
        }

        try {
            verificationCodeService.sendOtpCode(email);

            return new GenericResponse("Verification code resent", HttpStatus.OK);

        } catch (Exception e) {
            log.error("Failed to resend OTP for email={}. error={}", email, e.getMessage(), e);

            return new GenericResponse(
                    "Failed to resend code, please try again later.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public GenericResponse changePassword(ChangePasswordRequest request) {
        User user = securityUtil.getCurrentLoggedInUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw  new InvalidCredentialsException("Incorrect credential provided.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Password changed successfully.")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponse deleteAccount(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw  new InvalidCredentialsException("User not found with provided id.");
        }
        userRepository.deleteById(userId);
        return GenericResponse.builder()
                .isSuccess(true)
                .message("Account deleted successfully.")
                .httpStatus(HttpStatus.OK)
                .build();
    }

//    Helper Methods

    private User createUserTemplate(RegisterRequest signUpRequestDto) {
        String token = UserIdUtil.generateKudiUserId();
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER not found in database"));
        roles.add(role);
        return User.builder()
                .createdAt(LocalDateTime.now())
                .firstname(signUpRequestDto.getFirstname())
                .lastname(signUpRequestDto.getLastname())
                .phoneNumber(signUpRequestDto.getPhoneNumber())
                .roles(roles)
                .userUUID(token)
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .build();
    }

    private void validateLoginRequest(LoginRequest loginRequestDto) {
        if (loginRequestDto.getEmail() == null || loginRequestDto.getPassword() == null) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    private void sendOtpAndNotify(User userEntity) {
        verificationCodeService.sendOtpCode(userEntity.getEmail());
    }

    private GenericResponse createOtpSentResponse() {
        return GenericResponse.builder()
                .isSuccess(true)
                .message("OTP sent to your email. Please verify OTP to complete login.")
                .httpStatus(HttpStatus.CONTINUE)
                .build();
    }

    private boolean isInvalidPassword(String inputPassword, User userEntity) {
        return !passwordEncoder.matches(inputPassword, userEntity.getPassword());
    }

    private void handleFailedLoginAttempt(User userEntity) {
        userEntity.incrementFailedLoginAttempts();
        userRepository.save(userEntity);
    }

    private Authentication authenticateUser(LoginRequest loginRequestDto) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );
    }

    private String generateAccessToken(Authentication authentication) {
        return jwtUtils.generateJwtToken(authentication);
    }

    private List<String> extractRoles(UserDetailsImpl userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private void updateUserLastLogin(User userEntity) {
        userEntity.setLastLoginAt(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    private LoginResponseDto buildLoginResponseDto(User userEntity, String accessToken, RefreshToken savedRefreshToken, List<String> roles) {
        return LoginResponseDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .enabled(userEntity.isEnabled())
                .username(userEntity.getFullName())
                .profilePicture(userEntity.getProfilePicture())
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(savedRefreshToken.getToken())
                .userUUID(userEntity.getUserUUID())
                .roles(roles)
                .phoneNumber(userEntity.getPhoneNumber())
                .lastLoginAt(userEntity.getLastLoginAt())
                .createdAt(userEntity.getCreatedAt())
                .verified(userEntity.isVerified())
                .build();
    }

    private GenericResponse buildLoginSuccessResponse(LoginResponseDto responseDto) {
        return GenericResponse.builder()
                .isSuccess(true)
                .message("Sign in successfully!")
                .data(responseDto)
                .httpStatus(HttpStatus.OK)
                .build();
    }

}
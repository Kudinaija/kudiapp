package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.MediaUploadRequest;
import com.kudiapp.kudiapp.dto.request.authDTOS.UserUpdateRequest;
import com.kudiapp.kudiapp.exceptions.FailedProcessException;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.exceptions.UserNotFoundException;
import com.kudiapp.kudiapp.models.Media;
import com.kudiapp.kudiapp.models.User;
import com.kudiapp.kudiapp.repository.UserRepository;
import com.kudiapp.kudiapp.services.MediaService;
import com.kudiapp.kudiapp.services.UserService;
import com.kudiapp.kudiapp.utills.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final MediaService mediaService;

    public UserServiceImpl(UserRepository userRepository, SecurityUtil securityUtil, MediaService mediaService) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.mediaService = mediaService;
    }

    @Override
    public GenericResponse getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> users = userRepository.findAll(pageable);

        return new GenericResponse("Users fetched successfully", HttpStatus.OK, users);
    }

    @Override
    public GenericResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new GenericResponse("User found", HttpStatus.OK, user);
    }

    @Override
    public GenericResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstname(userUpdateRequest.getFirstname());
        user.setLastname(userUpdateRequest.getLastname());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new GenericResponse("User updated successfully", HttpStatus.OK, user);
    }

    @Override
    public GenericResponse toggleDisableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(!user.isEnabled());
        user.setUpdatedBy(securityUtil.getCurrentUserId());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new GenericResponse("User disabled successfully", HttpStatus.OK);
    }

    @Override
    public GenericResponse deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        return new GenericResponse("User deleted successfully", HttpStatus.OK, null);
    }

    @Override
    @Transactional
    public GenericResponse uploadUserProfilePicture(MultipartFile file, String description, String mediaType) {

        User user = securityUtil.getCurrentLoggedInUser();
            try {
                MediaUploadRequest mediaUploadRequest = new MediaUploadRequest();
                mediaUploadRequest.setDescription(description);
                mediaUploadRequest.setFile(file);
                mediaUploadRequest.setMediaType(mediaType);
                Media media = mediaService.uploadImage(mediaUploadRequest);

                user.setProfilePicture(media.getUrl());
                userRepository.save(user);

                return GenericResponse.builder()
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .message("User profile picture uploaded successfully")
                        .build();
            } catch (Exception e) {
                log.error("Error uploading profile picture: {}", e.getMessage(), e);
                throw new FailedProcessException("Failed to upload profile picture: " + e.getMessage());
            }
        }

    @Override
    @Transactional
    public GenericResponse enableTwoFactor(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnable2Fa()) {
            return GenericResponse.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Two-factor authentication is already enabled")
                    .isSuccess(false)
                    .build();
        }

        user.setEnable2Fa(true);
        userRepository.save(user);

        return GenericResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Two-factor authentication enabled successfully")
                .isSuccess(true)
                .build();
    }


    @Override
    @Transactional
    public GenericResponse disableTwoFactor(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEnable2Fa()) {
            return GenericResponse.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Two-factor authentication is already disabled")
                    .isSuccess(false)
                    .build();
        }

        user.setEnable2Fa(false);
        userRepository.save(user);

        return GenericResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Two-factor authentication disabled successfully")
                .isSuccess(true)
                .build();
    }
}

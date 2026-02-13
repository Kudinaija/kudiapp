package com.kudiapp.kudiapp.services;


import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.authDTOS.UserUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface UserService {

    GenericResponse getAllUsers(int page, int size);

    GenericResponse getUserById(Long id);

    GenericResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);

    GenericResponse deleteUser(Long id);

    GenericResponse uploadUserProfilePicture(MultipartFile file, String description, String mediaType);

    GenericResponse enableUser(Long id);

    GenericResponse disableUser(Long id);
}

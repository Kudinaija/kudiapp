package com.kudiapp.kudiapp.services;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.MediaUpdateRequest;
import com.kudiapp.kudiapp.dto.request.MediaUploadRequest;
import com.kudiapp.kudiapp.models.Media;
import org.springframework.stereotype.Component;

@Component
public interface MediaService {
    GenericResponse getMedia(Long id);
    GenericResponse getAllMedia();
    GenericResponse updateMedia(Long id, MediaUpdateRequest request);
    GenericResponse uploadMedia(MediaUploadRequest request);
    Media uploadImage(MediaUploadRequest request);
    GenericResponse deleteMedia(Long id);
}
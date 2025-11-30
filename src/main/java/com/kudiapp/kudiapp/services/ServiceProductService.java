package com.kudiapp.kudiapp.services;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.ServiceProductRequest;
import org.springframework.data.domain.Pageable;

public interface ServiceProductService {

    GenericResponse create(ServiceProductRequest request);

    GenericResponse update(Long id, ServiceProductRequest request);

    GenericResponse getById(Long id);

    GenericResponse delete(Long id);

    GenericResponse findAll(String category, String title, String urgency, Pageable pageable);
}

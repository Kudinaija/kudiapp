package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.ServiceProductRequest;
import com.kudiapp.kudiapp.services.ServiceProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/service-products")
@RequiredArgsConstructor
@Slf4j
public class ServiceProductAdminController {

    private final ServiceProductService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GenericResponse> create(@RequestBody ServiceProductRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse> update(
            @PathVariable Long id,
            @RequestBody ServiceProductRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<GenericResponse> findAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String urgency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.findAll(category, title, urgency, PageRequest.of(page, size))
        );
    }
}

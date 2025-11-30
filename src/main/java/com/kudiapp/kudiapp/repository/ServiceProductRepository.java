package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.models.ServiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProductRepository extends JpaRepository<ServiceProduct, Long>, JpaSpecificationExecutor<ServiceProduct> {
}

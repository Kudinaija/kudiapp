package com.kudiapp.kudiapp.models.approles;

import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "permissions")
public class PermissionItem extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String permissionName;

    @Column
    private String description;
}
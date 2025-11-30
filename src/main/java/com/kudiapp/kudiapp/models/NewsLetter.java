package com.kudiapp.kudiapp.models;

import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NewsLetter extends BaseEntity {

    private String name;
    private String message;
    private String email;
    private String phoneNumber;

    @Builder.Default
    private boolean attendedTo = false;

    private Long attendedToBy;
}
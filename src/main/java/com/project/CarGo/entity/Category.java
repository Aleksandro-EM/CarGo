package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Type is required")
    @Column(nullable = false)
    private CategoryType type;

    @NotBlank(message = "Subtype is required")
    @Column(name= "subtype", nullable = false)
    private String subtype;

    @Column(name = "image_url")
    private String imageUrl;

    @PrePersist
    @PreUpdate
    private void validateSubType() {
        if(type == null || subtype == null || !type.isSubtype(subtype)) {
            throw new IllegalArgumentException("Invalid subtype: " + subtype + " for category type: " + type);
        }
    }
}

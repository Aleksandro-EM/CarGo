package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name="categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_types_subtypes", columnNames = {"type", "subtype"})
})
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

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Subtype is required")
    @Column(name= "subtype", nullable = false)
    private CategorySubtype subtype;

    @Column(name = "image_url")
    private String imageUrl;
}

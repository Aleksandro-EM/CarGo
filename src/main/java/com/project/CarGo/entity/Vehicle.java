package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "vehicles")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Vehicle {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plate number is required")
    @Size(min = 6, max = 7, message = "Plate number must be 6 to 7 characters")
    @Pattern(regexp = "^[A-Z0-9]{6,7}$", message = "Username must only contain lowercase letters only")
    @Column(name= "plate_number", nullable = false, unique = true, length=7)
    private String plateNumber;

    @NotBlank(message = "Make is required")
    @Column(nullable = false, length=100)
    private String make;

    @NotBlank(message = "Model is required")
    @Column(nullable = false, length=100)
    private String model;

    @NotBlank(message = "Color is required")
    @Column(nullable = false, length=50)
    private String color;

    @NotNull(message = "Year is required")
    @Column(nullable = false)
    private Integer year;

    @Column(name="current_mileage")
    private Integer currentMileage;

    @ManyToOne(optional = false)
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @Column(name="daily_rate", precision=10, scale=2, nullable = false)
    private BigDecimal dailyRate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(name= "status", nullable = false)
    private VehicleStatus status;

    @DateTimeFormat
    private Date nextAvailableDate;

    @DateTimeFormat
    private Date creationDate;

    @DateTimeFormat
    private Date updateDate;
}

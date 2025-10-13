package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    @NotBlank(message = "Number plate is required")
    @Size(min = 6, max = 7, message = "Number plate must be 6 to 7 characters")
    @Pattern(regexp = "^([A-Z0-9]{6,7}|[A-Z0-9]{3}\\s[A-Z0-9]{3})$", message = "Number plate must only contain uppercase letters and numbers only")
    @Column(name= "number_plate", nullable = false, unique = true, length=7)
    private String numberPlate;

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
    @Min(value = 2000, message = "Year must be greater than 2000")
    @Max(value = 2099, message = "Year must be less than 2099")
    @Column(nullable = false)
    private Integer year;

    @Column(name="current_mileage")
    private Integer currentMileage;

    @NotNull(message = "Category is required")
    @ManyToOne(optional = false)
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @Column(name="daily_rate", precision=10, scale=2, nullable = false)
    private BigDecimal dailyRate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(name= "status", nullable = false)
    private VehicleStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date nextAvailableDate;

    @DateTimeFormat
    private Date creationDate;

    @DateTimeFormat
    private Date updateDate;

    @PrePersist
    protected void onCreate() {
        nextAvailableDate = new Date();
        creationDate = new Date();
        updateDate = new Date();
    }

    @PreUpdate
    void onUpdate() {
        updateDate = new Date();
    }

    @Transient
    private BigDecimal totalPrice;
}

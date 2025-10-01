package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "reservations" /*, uniqueConstraints = {} <-- remove the email constraint */)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Please select a User")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId")
    private User user;


    @jakarta.validation.constraints.NotNull(message="Vehicle Cannot be null.")
    @jakarta.validation.constraints.Positive
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required.")
    private ReservationStatus status;

    private double totalPrice;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="Start date cannot be null.")
    private java.time.LocalDate reservationStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message="End date cannot be null.")
    private java.time.LocalDate reservationEndDate;

    private String stripePaymentId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private java.time.OffsetDateTime creationDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private java.time.OffsetDateTime updateDate;

    @PrePersist
    void prePersist() {
        var now = java.time.OffsetDateTime.now();
        creationDate = now;
        updateDate = now;
    }

    @PreUpdate
    void preUpdate() {
        updateDate = java.time.OffsetDateTime.now();
    }
}

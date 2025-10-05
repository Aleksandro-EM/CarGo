package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "reservations")
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

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message="Start date cannot be null.")
    private Date reservationStartDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message="End date cannot be null.")
    private Date reservationEndDate;

    @Column(name = "stripe_payment_id", length = 64)
    private String stripePaymentId;

    @Column(name = "payment_status", length = 32)
    private String paymentStatus;

    @DateTimeFormat
    private Date creationDate;

    @DateTimeFormat
    private Date updateDate;

    @PrePersist
    void prePersist() {
        this.creationDate = new Date();
        this.updateDate = new Date();
    }

    @PreUpdate
    void onUpdate() {
        updateDate = new Date();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "hold_expires_at")
    private Date holdExpiresAt;

}
package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotBlank(message = "Reservation must be linked with a User ID.")
    long userId;

    @NotBlank(message = "Reservation must be linked to a valid vehicle ID")
    long vehicleId;

    @Enumerated(EnumType.STRING)
    ReservationStatus status;

    double totalPrice;

    @DateTimeFormat
    Date reservationStartDate;

    @DateTimeFormat
    Date reservationEndDate;

    String stripePaymentId;

    @DateTimeFormat
    Date creationDate;

    @DateTimeFormat
    Date updateDate;

}

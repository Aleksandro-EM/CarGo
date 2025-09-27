package com.project.CarGo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Email is required.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message="Please enter a valid email.")
    private String email;

    @NotBlank(message="First name is required.")
    @Size(min=2, max=30, message="Please enter a reasonably sized first name.")
    private String firstName;

    @NotBlank(message="Last name is required.")
    @Size(min=2, max=30, message="Please enter a reasonably sized last name.")
    private String lastName;

    @NotBlank(message="Phone number is required.")
    @Pattern(regexp = "[+]?(?:\\(\\d+(?:\\.\\d+)?\\)|\\d+(?:\\.\\d+)?)(?:[ -]?(?:\\(\\d+(?:\\.\\d+)?\\)|\\d+(?:\\.\\d+)?))*(?:[ ]?(?:x|ext)\\.?[ ]?\\d{1,5})?",
            message="Please enter a valid phone number")
    private String phoneNumber;

    @NotBlank(message="Password is required.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message="Password must include an uppercase character, a lowercase character, a number and a " +
                    "special character.")
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    private String driversLicenseUrl;

    @DateTimeFormat
    private Date creationDate;

    @DateTimeFormat
    private Date updateDate;

    @Transient
    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

}

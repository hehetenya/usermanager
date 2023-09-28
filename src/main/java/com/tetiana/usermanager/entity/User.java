package com.tetiana.usermanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email may not be blank")
    @Email(message = "Wrong email format")
    private String email;

    @NotBlank(message = "First name may not be blank")
    private String firstName;

    @NotBlank(message = "Last name may not be blank")
    private String lastName;

    @Past(message = "Birth date should be in past")
    @NotNull
    private LocalDate birthDate;

    private String address;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Wrong phone number format")
    private String phoneNumber;
}

package ro.mycode.solarsyncbroker.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ro.mycode.solarsyncbroker.users.model.UserType; // Corectat importul de la Hibernate la enum-ul tău real

public record AuthReqisterRequest(
        @NotNull(message = "Tipul utilizatorului este obligatoriu")
        UserType userType,

        @NotBlank(message = "Numele este obligatoriu")
        String firstName,

        @NotBlank(message = "Prenumele este obligatoriu")
        String lastName,

        @Email(message = "Formatul email-ului este invalid")
        @NotBlank(message = "Email-ul este obligatoriu")
        String email,

        @NotBlank(message = "Parola este obligatorie")
        @Size(min = 3, max = 50)
        String password,

        @NotNull(message = "Varsta este obligatorie")
        @Min(value = 18, message = "Trebuie sa ai cel putin 18 ani")
        int age
) {}
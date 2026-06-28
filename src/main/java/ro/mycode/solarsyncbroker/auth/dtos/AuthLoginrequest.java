package ro.mycode.solarsyncbroker.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginrequest(
        @NotBlank(message = "Email-ul este obligatoriu")
        @Size(min = 3, max = 100)
        String email,

        @NotBlank(message = "Parola este obligatorie")
        @Size(min = 3, max = 50)
        String password
) {}
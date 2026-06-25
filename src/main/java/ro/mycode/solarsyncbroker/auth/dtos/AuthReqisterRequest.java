package ro.mycode.solarsyncbroker.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.usertype.UserType;

public record AuthReqisterRequest(
        @NotNull
        UserType userType,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String password,

        @NotNull
        @Size(min = 3, max = 50)
        String age


) {
}

package ro.mycode.solarsyncbroker.house.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record HouseRequest(
        @NotBlank(message = "Numele casei este obligatoriu")
        String name,

        @NotNull(message = "Puterea PV este obligatorie")
        @Positive(message = "pvPeakPowerKw trebuie sa fie mai mare ca 0")
        Double pvPeakPowerKw,

        @NotNull(message = "Puterea maxima de import este obligatorie")
        @Positive(message = "maxImportPowerKw trebuie sa fie mai mare ca 0")
        Double maxImportPowerKw,

        @NotNull(message = "Puterea maxima de export este obligatorie")
        @Positive(message = "maxExportPowerKw trebuie sa fie mai mare ca 0")
        Double maxExportPowerKw,

        @NotNull(message = "ID-ul proprietarului este obligatoriu")
        Long ownerId
) {}
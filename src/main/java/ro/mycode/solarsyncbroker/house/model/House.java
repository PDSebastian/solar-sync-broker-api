package ro.mycode.solarsyncbroker.house.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ro.mycode.solarsyncbroker.users.model.User;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="houses")
public class House {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele este obligatoriu")
    @Size(min=1, max=100)
    private String name;

    @NotNull
    private Double pvPeakPowerKw;

    @NotNull
    private Double maxImportPowerKw;

    @NotNull
    private Double maxExportPowerKw;

    @Builder.Default
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Override
    public String toString() {
        return "House{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pvPeakPowerKw=" + pvPeakPowerKw +
                ", maxImportPowerKw=" + maxImportPowerKw +
                ", maxExportPowerKw=" + maxExportPowerKw +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return Objects.equals(id, house.id) && Objects.equals(name, house.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
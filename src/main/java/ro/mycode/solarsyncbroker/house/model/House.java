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

    @NotBlank(message = "Numele este obligstoriu")
    @Size(min=1, max=100)
    private String name;

    @NotNull
    private double pvPeakPowerKw;

    @NotNull
    private double maxImportPowerKw;

    @NotNull
    private double maxExportPowerKw;

    private boolean enabled=true;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "owner_id", unique = true, nullable = false)
    private User owner;


    @Override
    public String toString() {
        return "House{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pvPeakPowerKw=" + pvPeakPowerKw +
                ", maxImportPowerKw=" + maxImportPowerKw +
                ", minExportPowerKw=" + maxExportPowerKw +
                ", enabled=" + enabled +
                ", owner=" + owner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return Double.compare(pvPeakPowerKw, house.pvPeakPowerKw) == 0 && Double.compare(maxImportPowerKw, house.maxImportPowerKw) == 0 && Double.compare(maxExportPowerKw, house.maxExportPowerKw) == 0 && enabled == house.enabled && Objects.equals(id, house.id) && Objects.equals(name, house.name) && Objects.equals(owner, house.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pvPeakPowerKw, maxImportPowerKw, maxExportPowerKw, enabled, owner);
    }
}

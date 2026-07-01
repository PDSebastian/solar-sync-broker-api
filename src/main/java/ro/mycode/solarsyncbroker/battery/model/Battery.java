package ro.mycode.solarsyncbroker.battery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ro.mycode.solarsyncbroker.house.model.House;

import java.util.Objects;

@Entity
@Table(name="bateries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double socPercent;

    @NotNull
    private Double maxChargePowerKw;

    @NotNull
    private Double maxDischargePowerKw;

    @NotNull
    private Double efficientyPercent;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houseId", nullable = false)
    private House house;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Battery battery = (Battery) o;
        return Objects.equals(id, battery.id) && Objects.equals(socPercent, battery.socPercent) && Objects.equals(maxChargePowerKw, battery.maxChargePowerKw) && Objects.equals(maxDischargePowerKw, battery.maxDischargePowerKw) && Objects.equals(efficientyPercent, battery.efficientyPercent) && Objects.equals(house, battery.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, socPercent, maxChargePowerKw, maxDischargePowerKw, efficientyPercent, house);
    }

    @Override
    public String toString() {
        return "Battery{" +
                "id=" + id +
                ", socPercent=" + socPercent +
                ", maxChargePowerKw=" + maxChargePowerKw +
                ", maxDischargePowerKw=" + maxDischargePowerKw +
                ", efficientyPercent=" + efficientyPercent +
                ", house=" + house +
                '}';
    }

}

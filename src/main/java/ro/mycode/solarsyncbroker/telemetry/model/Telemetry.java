package ro.mycode.solarsyncbroker.telemetry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ro.mycode.solarsyncbroker.house.model.House;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Telemetry")
public class Telemetry {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double pvPowerKw;
    @NotNull
    private Double loadPowerKw;
    @NotNull
    private Double gridPowerKw;
    @NotNull
    private Double batterySocPercent;
    @NotNull
    private Double batteryPowerKw;
    @NotNull
    private LocalDateTime localDateTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;


    @Override
    public String toString() {
        return "Telemetry{" +
                "id=" + id +
                ", pvPowerKw=" + pvPowerKw +
                ", loadPowerKw=" + loadPowerKw +
                ", gridPowerKw=" + gridPowerKw +
                ", batterySocPercent=" + batterySocPercent +
                ", batteryPowerKw=" + batteryPowerKw +
                ", localDateTime=" + localDateTime +
                ", house=" + house +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Telemetry telemetry = (Telemetry) o;
        return Objects.equals(id, telemetry.id) && Objects.equals(pvPowerKw, telemetry.pvPowerKw) && Objects.equals(loadPowerKw, telemetry.loadPowerKw) && Objects.equals(gridPowerKw, telemetry.gridPowerKw) && Objects.equals(batterySocPercent, telemetry.batterySocPercent) && Objects.equals(batteryPowerKw, telemetry.batteryPowerKw) && Objects.equals(localDateTime, telemetry.localDateTime) && Objects.equals(house, telemetry.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pvPowerKw, loadPowerKw, gridPowerKw, batterySocPercent, batteryPowerKw, localDateTime, house);
    }

}

package ro.mycode.solarsyncbroker.battery.dtos;

public record BatteryCommandRequest(

        String commandType,
        Integer targetSoc
) {
}

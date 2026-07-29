package ro.mycode.solarsyncbroker.house.dtos;

public record HouseStateResponse(
        Long houseId,
        String name,
        Double currentLoadKw,
        boolean enabled
) {
}

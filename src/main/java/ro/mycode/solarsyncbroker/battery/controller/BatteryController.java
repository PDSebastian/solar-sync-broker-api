package ro.mycode.solarsyncbroker.battery.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommandRequest;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryConfigurationRequest;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.dtos.CommandExecutionResponse;
import ro.mycode.solarsyncbroker.battery.service.commandService.BatteryCommandService;
import ro.mycode.solarsyncbroker.battery.service.queryService.BatteryQueryService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/houses")
@Slf4j
public class BatteryController {

    private final BatteryQueryService batteryQueryService;
    private final BatteryCommandService batteryCommandService;

    public BatteryController(BatteryQueryService batteryQueryService, BatteryCommandService batteryCommandService) {
        this.batteryQueryService = batteryQueryService;
        this.batteryCommandService = batteryCommandService;
    }

    @GetMapping("/{houseId}/battery")
    @PreAuthorize("hasAuthority('BATTERY_VIEW')")
    public ResponseEntity<BatteryResponse> getBattery(@PathVariable Long houseId, Principal principal) {
        BatteryResponse response = batteryQueryService.getBatteryByHouseId(houseId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{houseId}/battery/configuration")
    @PreAuthorize("hasAuthority('BATTERY_CONFIG')")
    public ResponseEntity<BatteryResponse> updateConfiguration(@PathVariable Long houseId, @Valid @RequestBody BatteryConfigurationRequest request) {
        BatteryResponse response = batteryCommandService.updateConfiguration(houseId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{houseId}/battery/commands")
    @PreAuthorize("hasAuthority('BATTERY_COMMAND')")
    public ResponseEntity<CommandExecutionResponse> sendCommand(
            @PathVariable Long houseId,
            @RequestBody @Valid BatteryCommandRequest request,
            Principal principal
    ) {
        CommandExecutionResponse response = batteryCommandService.executeCommand(houseId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{houseId}/battery/commands")
    @PreAuthorize("hasAuthority('BATTERY_COMMAND')")
    public ResponseEntity<List<CommandExecutionResponse>> getCommands(@PathVariable Long houseId) {
        List<CommandExecutionResponse> responses = batteryCommandService.getCommandsForHouse(houseId);
        return ResponseEntity.ok(responses);
    }
}
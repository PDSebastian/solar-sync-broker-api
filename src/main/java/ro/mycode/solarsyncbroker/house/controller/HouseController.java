package ro.mycode.solarsyncbroker.house.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.service.commandService.HouseCommandService;
import ro.mycode.solarsyncbroker.house.service.queryService.HouseQueryService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/houses")
@Slf4j
public class HouseController {

    HouseCommandService houseCommandService;
    HouseQueryService houseQueryService;

    public HouseController(HouseCommandService houseCommandService, HouseQueryService houseQueryService) {
        this.houseCommandService = houseCommandService;
        this.houseQueryService = houseQueryService;
    }



    @GetMapping
    @PreAuthorize("hasAuthority('house:view')")
    public ResponseEntity<List<HouseResponse>> getAllHouses() {
        return ResponseEntity.ok(houseQueryService.getAllHouses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('house:view')")
    public ResponseEntity<HouseResponse> getHouseById(@PathVariable Long id, String email) {
        return ResponseEntity.ok(houseQueryService.getHouseForCaller(id,email));
    }


    @PostMapping
    @PreAuthorize("hasAuthority('house:manage')")
    public ResponseEntity<HouseResponse> createHouse(@Valid @RequestBody HouseRequest houseRequest) {
        HouseResponse houseResponse = houseCommandService.createHouse(houseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(houseResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('house:manage')")
    public ResponseEntity<HouseResponse> updateHouse(@PathVariable Long id, @Valid @RequestBody HouseRequest houseRequest) {
        return ResponseEntity.ok(houseCommandService.updateHouse(id, houseRequest));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('house:manage')")
    public ResponseEntity<HouseResponse> patchHouse(@PathVariable Long id, @RequestBody HouseRequest houseRequest) {
        return ResponseEntity.ok(houseCommandService.patchHouse(id, houseRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('house:manage')")
    public ResponseEntity<Void> deleteHouse(@PathVariable Long id) {
        houseCommandService.deleteHouse(id);
        return ResponseEntity.noContent().build();
    }
}
package ro.mycode.solarsyncbroker.house.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.service.commandService.HouseCommandService;
import ro.mycode.solarsyncbroker.house.service.queryService.HouseQueryService;

@RestController
@RequestMapping("/api/v1/houses")
public class HouseController {
    private HouseCommandService houseCommandService;
    private HouseQueryService  houseQueryService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('house:manage')")
    public ResponseEntity<HouseResponse>createHouse(@Valid @RequestBody HouseRequest houseRequest ) {
        HouseResponse houseResponse = houseCommandService.createHouse(houseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(houseResponse);


    }


}

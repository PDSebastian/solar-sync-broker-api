package ro.mycode.solarsyncbroker.unitTests.controllerTests;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mycode.solarsyncbroker.house.controller.HouseController;
import ro.mycode.solarsyncbroker.house.service.commandService.HouseCommandService;
import ro.mycode.solarsyncbroker.house.service.queryService.HouseQueryService;

@ExtendWith(MockitoExtension.class)
public class HouseControllerTest {
  @Mock
  HouseCommandService houseCommandService;
  @Mock
  HouseQueryService houseQueryService;

  @InjectMocks
  HouseController houseController;

  @Test
    void createHouse() throws Exception {




  }



}

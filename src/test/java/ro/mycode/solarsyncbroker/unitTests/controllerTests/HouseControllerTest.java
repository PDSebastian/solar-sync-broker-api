package ro.mycode.solarsyncbroker.unitTests.controllerTests;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.mycode.solarsyncbroker.house.controller.HouseController;
import ro.mycode.solarsyncbroker.house.dtos.HouseRequest;
import ro.mycode.solarsyncbroker.house.dtos.HouseResponse;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.service.commandService.HouseCommandService;
import ro.mycode.solarsyncbroker.house.service.queryService.HouseQueryService;
import ro.mycode.solarsyncbroker.system.exceptions.GlobalExceptionHandler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HouseControllerTest {




  @Mock
 private HouseCommandService houseCommandService;
  @Mock
 private HouseQueryService houseQueryService;

  @InjectMocks
 private HouseController houseController;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(houseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    void createHouseSuccess() throws Exception {
        Long houseId = 1L;
        Long userId = 2L;

        HouseRequest houseRequest=new HouseRequest("casa",7.5,12.0,5.0,userId);
        HouseResponse houseResponse=new HouseResponse(houseId,"casa noua",7.5,12.0,5.0,true,userId);

        when(houseCommandService.createHouse(houseRequest)).thenReturn(houseResponse);

        when(houseCommandService.createHouse(houseRequest)).thenReturn(houseResponse);
        mockMvc.perform(post("/api/v1/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(houseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(houseId))
                .andExpect(jsonPath("$.name").value("casa noua"));

        verify(houseCommandService).createHouse(houseRequest);

    }


  @Test
    void updateHouseSuccess() throws Exception {
     Long houseId = 1L;
     Long userId = 2L;
      HouseRequest houseRequest=new HouseRequest("casa",7.5,12.0,5.0,userId);
      HouseResponse houseResponse=new HouseResponse(houseId,"casa noua",7.5,12.0,5.0,true,userId);

      when(houseCommandService.updateHouse(houseId,houseRequest)).thenReturn(houseResponse);
      mockMvc.perform(put("/api/v1/houses/"+ houseId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(houseRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(houseId))
              .andExpect(jsonPath("$.name").value("casa noua"));

      verify(houseCommandService).updateHouse(houseId,houseRequest);
  }
  @Test
    void testPatchHouseSuccess() throws Exception {
      Long houseId = 1L;
      HouseRequest houseRequest=new HouseRequest("Casa noua", null,null,null,null);
      HouseResponse houseResponse=new HouseResponse(houseId,"Casa noua",7.5,12.0,5.0,true,null);
      when(houseCommandService.patchHouse(houseId,houseRequest)).thenReturn(houseResponse);

      mockMvc.perform(patch("/api/v1/houses/"+ houseId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(houseRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(houseId))
              .andExpect(jsonPath("$.name").value("Casa noua"));

      verify(houseCommandService).patchHouse(houseId,houseRequest);
    }
    @Test
    void deleteHouseSuccess() throws Exception {
        Long houseId = 1L;
        mockMvc.perform(delete("/api/v1/houses/"+ houseId)).andExpect(status().isNoContent());
        verify(houseCommandService).deleteHouse(houseId);
    }




}

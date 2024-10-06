package com.apromore.compliance_centre.config;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.apromore.compliance_centre.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
@Import({ GlobalControllerExceptionHandler.class, GlobalControllerExceptionHandlerTest.DummyController.class })
public class GlobalControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    public static class DummyController {

        @GetMapping("/provokeResponseStatusException")
        public String provokeResponseStatusException() {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }

        @GetMapping("/provokeException")
        public Response<String> provokeException() throws Exception {
            throw new Exception("Internal server error");
        }
    }

    @Test
    public void handleResponseStatusExceptionTest() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/provokeResponseStatusException"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("NOT_FOUND"))
            .andExpect(jsonPath("$.errorDetails[0].field").value("error"))
            .andExpect(jsonPath("$.errorDetails[0].description").value("Entity not found"));
    }

    @Test
    public void handleExceptionTest() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/provokeException"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.errorDetails[0].field").value("error"))
            .andExpect(jsonPath("$.errorDetails[0].description").value("Internal server error"));
    }
}

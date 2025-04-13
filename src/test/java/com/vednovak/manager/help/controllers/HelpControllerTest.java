package com.vednovak.manager.help.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToSwaggerDocumentation() throws Exception {
        mockMvc.perform(get("/v1/help"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "/swagger-ui/index.html"))
                .andExpect(header().string(HttpHeaders.CONNECTION, "close"));
    }
}
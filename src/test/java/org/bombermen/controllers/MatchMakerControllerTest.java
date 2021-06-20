package org.bombermen.controllers;

import org.bombermen.services.GameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(MatchMakerController.class)
class MatchMakerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = GameService.getInstance();
    }

    @AfterEach
    void tearDown() {
        gameService.getGames().clear();
        gameService = null;
        mockMvc = null;
    }

    @Test
    void getGameIdTest() throws Exception {
        String url = "/matchmaker/join";
        String param = "name=";
        String attribute = "test123";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content(param+attribute)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String outputInJson = response.getContentAsString();
        assertEquals(attribute, outputInJson);
    }
}
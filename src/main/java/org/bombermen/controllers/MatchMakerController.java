package org.bombermen.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bombermen.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class MatchMakerController {
    private static final Logger logger = LoggerFactory.getLogger(MatchMakerController.class);
    private final GameService gameService = GameService.getInstance();

//    public MatchMakerController() {
//        this.gameService = GameService.getInstance();
//    }

    @PostMapping(value="/matchmaker/join", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String getGameId(@RequestParam HashMap<String, String> data) throws IOException {
        logger.debug("In controller1");
        String gameID = gameService.create(data.get("name"));

        return gameID;
    }

    @GetMapping(value="/test")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String test() {
        logger.debug("In controller1");
        return "test getting through";
    }
//    private String getJsonValueByKey(String dataAsString, String key) throws IOException {
//        byte[] jsonData = dataAsString.getBytes();
//
//        //create ObjectMapper instance
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        //read JSON like DOM Parser
//        JsonNode rootNode = objectMapper.readTree(jsonData);
//        JsonNode idNode = rootNode.path(key);
//
//        return idNode.asText();
//    }
}

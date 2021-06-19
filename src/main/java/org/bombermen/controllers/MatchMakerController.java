package org.bombermen.controllers;

import org.bombermen.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.HashMap;

@Controller
@CrossOrigin(origins = "*")
public class MatchMakerController {
    //private static final Logger logger = LoggerFactory.getLogger(MatchMakerController.class);
    private final GameService gameService = GameService.getInstance();

    @PostMapping(value="/matchmaker/join", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String getGameId(@RequestParam HashMap<String, String> data){
        String gameID = gameService.create(data.get("name"));

        return gameID;
    }

}

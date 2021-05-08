package org.bombermen;

import org.bombermen.services.GameService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameServerApp{
    public static void main(String[] args) {
        SpringApplication.run(GameServerApp.class, args);
    }

}

package org.bombermen.message;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonbTester;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


@SpringJUnitConfig
@JsonTest
class MessageTest {
    private Message message;
    private Topic topic;

    @Autowired
    private JacksonTester<Message> jsonTest;

    @Value("classpath:moveMessage.json")
    private Resource moveMessage;
    @Value("classpath:plantBombMessage.json")
    private Resource plantBombMessage;


    @Test
    void checkState() {
        Topic topic = Topic.MOVE;
        String data = "{\"direction\":\"LEFT\"}";
        String playerName = "player0";
        message = new Message(topic, data);
        message.setPlayerName(playerName);
        assertEquals(topic, message.getTopic());
        assertEquals(data, message.getData());
        assertEquals(playerName, message.getPlayerName());
    }

    @Test
    void deserializeMoveMessageTest() throws IOException {
        final Message message = jsonTest.readObject(moveMessage);
        assertAll(
                () -> assertNotNull(message),
                () -> assertEquals(Topic.MOVE, message.getTopic()),
                () -> assertEquals("{\"direction\":\"DOWN\"}", message.getData())
                );
    }

    @Test
    void deserializePlantBombMessageTest() throws IOException {
        final Message message = jsonTest.readObject(plantBombMessage);
        assertAll(
                () -> assertNotNull(message),
                () -> assertEquals(Topic.PLANT_BOMB, message.getTopic()),
                () -> assertEquals("{}", message.getData())
        );
    }

}
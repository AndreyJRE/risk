package com.unima.risk6.network.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.message.StandardMessage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Tests for object serialization
 *
 * @author jferch
 */


public class SerializationTest {
  @BeforeAll
  static void setUp() {
    try {

    } catch (Exception e) {
    }

  }


  @Test
  void testSerializationWithStatus() {
    assertEquals("{\"statusCode\":200,\"content\":\"tetest\",\"contentType\":\"DEFAULT\"}", Serializer.serialize(new StandardMessage("tetest", 200)));
  }

  @Test
  void testSerializationWithoutStatus() {
    assertEquals("{\"statusCode\":-1,\"content\":\"tetest\",\"contentType\":\"DEFAULT\"}", Serializer.serialize(new StandardMessage("tetest")));

  }

  @Test
  void testSerializationAndDeserialization() {
    StandardMessage standard =  new StandardMessage("tetest");
    String json = Serializer.serialize(standard);
    StandardMessage s2 = (StandardMessage) Deserializer.deserialize(json);
    System.out.println(s2.getStatusCode() + (String)s2.getContent());

    //assertEquals(standard, s2);
    assertTrue(standard.equals(s2));

  }
/*
  @Test
  void testPlayerSerialization() {
    Player player = new Player("Jakob");
    StandardMessage<Player> s = new StandardMessage<Player>(player);
    String json = Serializer.serialize(s);
    System.out.println(json);
    //StandardMessage s2 = (StandardMessage) Deserializer.deserialize(json);
    //System.out.println(s2.getStatusCode() + (String)s2.getContent());
    StandardMessage message = (StandardMessage<Player>) Deserializer.deserialize(json);
    //Player player2 = (Player) message.getContent();
    System.out.println("test");
    //TODO
    //assertTrue(player.equals(player2));

  }
  @Test
  void testEasyBotSerialization() {
    System.out.println("EasyBotSrialization");
    ArrayList<String> users =  new ArrayList<String>(Arrays.asList("Andrey","Max","Fung"));
    ArrayList<AiBot> bots = new ArrayList<AiBot>();
    GameState gameState = GameConfiguration.configureGame(users, bots);
    gameState.setCurrentPhase(GamePhase.CLAIMPHASE);
    EasyBot bot = new EasyBot(gameState);
    StandardMessage<Player> s = new StandardMessage<Player>(bot);
    String json = Serializer.serialize(s);
    System.out.println(json);
    //StandardMessage s2 = (StandardMessage) Deserializer.deserialize(json);
    //System.out.println(s2.getStatusCode() + (String)s2.getContent());
    StandardMessage message = (StandardMessage) Deserializer.deserialize(json);
    //TODO
    assertTrue(bot.equals(message.getContent()));

  }

 */
  @Test
  void testAttackSerialization(){
      Country c1 = new Country(CountryName.ALASKA);
      c1.setContinent(new Continent(ContinentName.AFRICA));
      Country c2 = new Country(CountryName.ICELAND);
      c1.setContinent(new Continent(ContinentName.AFRICA));

      Attack a = new Attack(c1, c2, 69);
      StandardMessage<Attack> message = new StandardMessage<>(a);
      System.out.println(Serializer.serialize(message));

  }
}
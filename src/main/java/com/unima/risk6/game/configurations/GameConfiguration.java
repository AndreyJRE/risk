package com.unima.risk6.game.configurations;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.UserDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * GameConfiguration is a utility class responsible for configuring the initial state of the game.
 *
 * @author astoyano
 */
public class GameConfiguration {

  private static GameState gameState;

  private static final String COUNTRIES_JSON_PATH = "/com/unima/risk6/json/countries.json";

  private static final List<GameStateObserver> observers = new ArrayList<>();
  private static UserDto myGameUser;

  /**
   * Configures the game by initializing the countries, continents, players and creating a GameState
   * object
   *
   * @param users List of usernames from users
   * @param bots  List of different bots
   */
  public static GameState configureGame(List<String> users, List<AiBot> bots) {
    CountriesConfiguration countriesConfiguration = new CountriesConfiguration(COUNTRIES_JSON_PATH);
    countriesConfiguration.configureCountriesAndContinents();
    Set<Country> countries = countriesConfiguration.getCountries();
    Set<Continent> continents = countriesConfiguration.getContinents();
    PlayersConfiguration playersConfiguration = new PlayersConfiguration(users, bots);
    playersConfiguration.configure();
    Queue<Player> players = playersConfiguration.getPlayers();
    return new GameState(countries, continents, players);
  }

  /**
   * Gets the game state
   *
   * @return The game state
   */
  public static GameState getGameState() {
    return gameState;
  }

  /**
   * Sets the game state and notifies the observers
   *
   * @param gameState The game state
   */
  public static void setGameState(GameState gameState) {
    GameConfiguration.gameState = gameState;
    notifyObservers();
  }

  /**
   * Adds a Game Stete observer to the list of observers
   *
   * @param observer The Game State observer
   */
  public static void addObserver(GameStateObserver observer) {
    observers.add(observer);
  }

  public static void removeObserver(GameStateObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies the observers of the game state
   */
  private static void notifyObservers() {
    for (GameStateObserver observer : observers) {
      observer.update(gameState);
    }
  }

  /**
   * Gets the user that is playing the game
   *
   * @return The user that is playing the game
   */
  public static UserDto getMyGameUser() {
    return myGameUser;
  }

  /**
   * Sets the user that is playing the game
   *
   * @param myGameUser The user that is playing the game
   */
  public static void setMyGameUser(UserDto myGameUser) {
    GameConfiguration.myGameUser = myGameUser;
  }
}

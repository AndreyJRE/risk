package com.unima.risk6.network.server;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.HandController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.Statistic;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.server.exceptions.InvalidMoveException;
import java.util.ArrayList;
import java.util.Set;


public class MoveProcessor {

  private final GameController gameController;
  private final PlayerController playerController;
  private final DeckController deckController;

  public MoveProcessor(PlayerController playerController, GameController gameController,
      DeckController deckController) {

    this.gameController = gameController;
    this.deckController = deckController;
    this.playerController = playerController;
  }

  public void processFortify(Fortify fortify) throws InvalidMoveException {
    //TODO Unterscheiden ob in Fortify Phase

    //Validate if the fortify is legal
    Country incoming = getCountryByCountryName(fortify.getIncoming().getCountryName());

    Country outgoing = getCountryByCountryName(fortify.getOutgoing().getCountryName());
    if (fortify.getTroopsToMove() < outgoing.getTroops() && outgoing
        .getPlayer().equals(incoming.getPlayer()) && incoming
        .getAdjacentCountries().contains(outgoing)) {
      incoming.changeTroops(fortify.getTroopsToMove());
      outgoing.changeTroops(-fortify.getTroopsToMove());
      gameController.addLastMove(fortify);

      Player currentPlayer = gameController.getCurrentPlayer();
      if (currentPlayer.getCurrentPhase().equals(GamePhase.FORTIFY_PHASE)) {
        processEndPhase(new EndPhase(FORTIFY_PHASE));
      }
    } else {
      throw new InvalidMoveException("The Fortify is not valid");
    }

  }

  public void processReinforce(Reinforce reinforce) throws InvalidMoveException {
    //TODO Schick gameState an Client
    Player currentPlayer = gameController.getCurrentPlayer();

    if (currentPlayer.getCurrentPhase().equals(REINFORCEMENT_PHASE)
        || currentPlayer.getCurrentPhase().equals(CLAIM_PHASE)) {
      gameController.addLastMove(reinforce);
      //Should process a Reinforce during the ClaimPhase differently
      Country countryToReinforce = getCountryByCountryName(reinforce.getCountry().getCountryName());
      if (currentPlayer.getCurrentPhase().equals(CLAIM_PHASE)) {
        if (!countryToReinforce.hasPlayer()) {
          countryToReinforce.setPlayer(currentPlayer);
          playerController.addCountry(countryToReinforce);
        }
        currentPlayer.setInitialTroops(currentPlayer.getInitialTroops() - 1);
        countryToReinforce.setTroops(countryToReinforce.getTroops() + 1);
        this.processEndPhase(new EndPhase(CLAIM_PHASE));


      } else {
        countryToReinforce.setTroops(countryToReinforce.getTroops() + reinforce.getToAdd());
        currentPlayer.setDeployableTroops(
            currentPlayer.getDeployableTroops() - reinforce.getToAdd());
        if (currentPlayer.getDeployableTroops() == 0) {
          processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));
        }
      }
    } else {
      throw new InvalidMoveException("The Reinforce is not valid");
    }
  }

  public void processAttack(Attack attack) throws InvalidMoveException {
    Country attackingCountry = getCountryByCountryName(
        attack.getAttackingCountry().getCountryName());

    Country defendingCountry = getCountryByCountryName(
        attack.getDefendingCountry().getCountryName());

    Player attacker = attackingCountry.getPlayer();
    Player defender = defendingCountry.getPlayer();

    //check if attack is legal.
    if (attacker.equals(gameController.getCurrentPlayer()) && !attacker.equals(defender)
        && defendingCountry.getAdjacentCountries().contains(attackingCountry)
        && attack.getTroopNumber() <= attackingCountry.getTroops() - 1) {
      attack.calculateLosses();

      gameController.addLastMove(attack);

      attackingCountry.changeTroops(-attack.getAttackerLosses());
      defendingCountry.changeTroops(-attack.getDefenderLosses());
      //Increase statistics
      Statistic attackerStatistic = attacker.getStatistic();
      Statistic defenderStatistic = defender.getStatistic();
      //Increase statistics for troopsLost
      attackerStatistic.setTroopsLost(
          attackerStatistic.getTroopsLost() + attack.getAttackerLosses());
      defenderStatistic.setTroopsLost(
          defenderStatistic.getTroopsLost() + attack.getDefenderLosses());

      if (attack.getHasConquered()) {
        gameController.getCurrentPlayer().setHasConquered(true);
        defendingCountry.setPlayer(attacker);

        playerController.addCountry(defendingCountry);
        playerController.setPlayer(defender);
        playerController.removeCountry(defendingCountry);
        playerController.setPlayer(attacker);

        //Increase statistic for countriesLost and countriesWon
        defenderStatistic.setCountriesLost(defenderStatistic.getCountriesLost() + 1);
        defenderStatistic.setCountriesWon(attackerStatistic.getCountriesWon() + 1);

        //Forced Fortify after attack and takeover
        Fortify forcedFortify = new Fortify(attackingCountry, defendingCountry,
            attack.getTroopNumber());
        processFortify(forcedFortify);
        //TODO OPTIONAL Fortify NOW Attack has won HOW TO SIGNAL?

      }
      playerController.setPlayer(defender);
      if (playerController.getNumberOfCountries() == 0) {
        gameController.removeLostPlayer(defender);
      }
      playerController.setPlayer(attacker);
    } else {
      throw new InvalidMoveException("The Attack is not valid");

    }

  }

  public void drawCard() {
    Card drawnCard = deckController.removeCardOnTop();
    playerController.getHandController().addCard(drawnCard);
    if (deckController.isEmpty()) {
      deckController.initDeck();
    }
  }

  public void processHandIn(HandIn handIn) throws InvalidMoveException {
    HandController handController = playerController.getHandController();
    ArrayList<Card> cardsOfPlayer = new ArrayList<>();
    handController.selectCardsFromCardList(handIn.getCards());
    //Checks if HandIn is valid.
    if (gameController.getCurrentPlayer().getCurrentPhase().equals(REINFORCEMENT_PHASE)
        && handController.isExchangeable()) {

      gameController.addLastMove(handIn);

      Set<Country> countries = playerController.getPlayer().getCountries();
      if (!handController.getBonusCountries(countries).isEmpty()) {
        handController.getBonusCountries(countries).forEach(n -> {
          playerController.changeDeployableTroops(2);

          try {
            this.processReinforce(new Reinforce(getCountryByCountryName(n), 2));
          } catch (InvalidMoveException e) {
            System.err.println("Wrong Reinforce in HandIn. Should not throw this exception");
          }

        });

      }
      int numberOfHandIn = gameController.getGameState().getNumberOfHandIns();
      int diff = 0;
      if (numberOfHandIn > 5) {
        diff = 15 + 5 * (numberOfHandIn - 6);
      } else {
        diff = 2 + 2 * (numberOfHandIn);
      }
      playerController.changeDeployableTroops(diff);
      //Increase troopsGained statistic according to troops gotten through card Exchange
      Statistic statisticOfCurrentPlayer = playerController.getPlayer().getStatistic();
      statisticOfCurrentPlayer.setTroopsGained(
          statisticOfCurrentPlayer.getTroopsGained() + diff);

      gameController.getGameState().setNumberOfHandIns(numberOfHandIn + 1);
      handController.exchangeCards();
    } else {
      handController.deselectAllCards();
      throw new InvalidMoveException("The HandIn is not valid");
    }

  }

  public void processEndPhase(EndPhase endPhase) {
    Player currentPlayer = gameController.getCurrentPlayer();
    if (currentPlayer.getHasConquered() && endPhase.getPhaseToEnd().equals(ATTACK_PHASE)) {
      this.drawCard();
      currentPlayer.setHasConquered(false);
    }

    gameController.addLastMove(endPhase);
    gameController.nextPhase();
    playerController.setPlayer(gameController.getCurrentPlayer());

  }


  public Country getCountryByCountryName(CountryName countryName) {
    final Country[] country = new Country[1];
    gameController.getGameState().getCountries().stream()
        .filter(n -> n.getCountryName().equals(countryName))
        .forEach(n -> country[0] = n);
    return country[0];
  }

  public void clearLastMoves() {
    gameController.getGameState().getLastMoves().clear();
  }


}


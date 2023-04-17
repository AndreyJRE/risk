package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The Attack class represents an attack move in the Risk game. It stores the attacking and
 * defending countries, the number of troops involved in the attack, and the results of the dice
 * rolls.
 *
 * @author wphung
 */
public class Attack extends Move {

  private final Country attackingCountry;
  private final Country defendingCountry;
  private int attackerLosses;
  private int defenderLosses;
  private ArrayList<Integer> attackDiceResult;
  private ArrayList<Integer> defendDiceResult;
  private final int troopNumber;


  /**
   * Constructs an Attack object with the specified attacking country, defending country, and number
   * of troops.
   *
   * @param attackingCountry the attacking country involved in the attack
   * @param defendingCountry the defending country being attacked
   * @param troopNumber      the number of troops involved in the attack
   */
  public Attack(Country attackingCountry, Country defendingCountry, int troopNumber) {
    this.attackingCountry = attackingCountry;
    this.defendingCountry = defendingCountry;
    this.troopNumber = troopNumber;
    this.attackDiceResult = new ArrayList<Integer>();
    this.defendDiceResult = new ArrayList<Integer>();
    attackerLosses = 0;
    defenderLosses = 0;
  }

  /**
   * Calculates the number of troops lost by the attacker and the defender based on the dice rolls.
   */
  public void calculateLosses() {

    switch (troopNumber) {
      case 1:
        attackDiceResult.add(Dice.rollDice());
        defendDiceResult.add(Dice.rollDice());
        sortDicelist();
        compareDice(0);
        break;

      case 2:
        for (int i = 0; i < 2; i++) {
          attackDiceResult.add(Dice.rollDice());
        }
        defendDiceResult.add(Dice.rollDice());
        if (defendingCountry.getTroops() > 1) {
          defendDiceResult.add(Dice.rollDice());
        }
        sortDicelist();
        if (defendingCountry.getTroops() > 1) {
          for (int i = 0; i < 2; i++) {
            compareDice(i);
          }
        } else {
          compareDice(0);
        }

        break;
      case 3:

        for (int i = 0; i < 3; i++) {
          attackDiceResult.add(Dice.rollDice());
        }
        defendDiceResult.add(Dice.rollDice());
        if (defendingCountry.getTroops() > 1) {
          defendDiceResult.add(Dice.rollDice());
        }
        sortDicelist();

        if (defendingCountry.getTroops() > 1) {
          for (int i = 0; i < 2; i++) {
            compareDice(i);
          }
        } else {
          compareDice(0);
        }
        break;
      default:
        break;
    }

  }

  /**
   * Compares the dice roll results for the attacker and defender and increments the appropriate
   * loss counter.
   *
   * @param n the index of the dice roll to compare
   */
  public void compareDice(int n) {
    if (attackDiceResult.get(n) > defendDiceResult.get(n)) {
      defenderLosses++;
    } else {
      attackerLosses++;
    }
  }

  /**
   * Sorts the dice roll lists in descending order.
   */
  public void sortDicelist() {
    Collections.sort(defendDiceResult, (x, y) -> y - x);
    Collections.sort(attackDiceResult, (x, y) -> y - x);
  }

  /**
   * Returns the list of dice rolls for the attacker.
   *
   * @return the list of dice rolls for the attacker
   */
  public ArrayList<Integer> getAttackDiceResult() {
    return attackDiceResult;
  }

  /**
   * Returns the list of dice rolls for the defender.
   *
   * @return the list of dice rolls for the defender
   */
  public ArrayList<Integer> getDefendDiceResult() {
    return defendDiceResult;
  }

  /**
   * Returns the attacking country involved in the attack.
   *
   * @return the attacking country involved in the attack
   */
  public Country getAttackingCountry() {
    return attackingCountry;
  }

  /**
   * Returns the defending country being attacked.
   *
   * @return the defending country being attacked
   */
  public Country getDefendingCountry() {
    return defendingCountry;
  }

  /**
   * Returns the number of troops lost by the attacker in the attack.
   *
   * @return the number of troops lost by the attacker in the attack
   */
  public int getAttackerLosses() {
    return attackerLosses;
  }

  /**
   * Returns the number of troops lost by the defender in the attack.
   *
   * @return the number of troops lost by the defender in the attack
   */
  public int getDefenderLosses() {
    return defenderLosses;
  }

  /**
   * Returns the number of troops involved in the attack.
   *
   * @return the number of troops involved in the attack
   */
  public int getTroopNumber() {
    return troopNumber;
  }

  public boolean battleWon() {
    return true; // TEMP METHOD
  }
}

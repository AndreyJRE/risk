package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;

public class Fortify extends Move {

  public Country outgoing;
  public Country incoming;
  public int troopsToMove;

  public Fortify(Country outgoing, Country incoming, int troopsToMove) {
    this.outgoing = outgoing;
    this.incoming = incoming;
    this.troopsToMove = troopsToMove;
  }

  public Country getOutgoing() {
    return outgoing;
  }

  public Country getIncoming() {
    return incoming;
  }

  /**
   * @return Returns how many troops the Player wants to move from the origin country to country to
   * fortify
   * @author Weng Phung
   */
  public int getTroopsToMove() {
    return troopsToMove;
  }
}

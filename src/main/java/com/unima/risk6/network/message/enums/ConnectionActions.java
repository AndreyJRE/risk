package com.unima.risk6.network.message.enums;

public enum ConnectionActions {
  //Server Side Join
  JOIN_SERVER_LOBBY, JOIN_GAME_LOBBY, CREATE_GAME_LOBBY, START_GAME,

  //Server Side Leave
  LEAVE_SERVER_LOBBY, LEAVE_GAME_LOBBY, LEAVE_GAME,

  //Client Side Accept
  ACCEPT_JOIN_SERVER_LOBBY, ACCEPT_JOIN_GAME_LOBBY, ACCEPT_CREATE_LOBBY, ACCEPT_START_GAME, ACCEPT_UPDATE_SERVER_LOBBY,

  //CLient Side Accept leave
  //ACCEPT_LEAVE_SERVER_LOBBY, ACCEPT_LEAVE_GAME_LOBBY,

  //Client Side Drop
  DROP_USER_SERVER_LOBBY, DROP_USER_GAME_LOBBY,

  //Configuration
  JOIN_BOT_GAME_LOBBY,

  //Currently not Used
  ACCEPT_USER_GAME, REMOVE_BOT_FROM_LOBBY, ACCEPT_JOIN_GAME
}

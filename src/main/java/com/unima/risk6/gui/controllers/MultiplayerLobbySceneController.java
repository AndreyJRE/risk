package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showErrorDialog;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.configurations.observers.GameLobbyObserver;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.LobbyUserStatisticScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class MultiplayerLobbySceneController implements GameLobbyObserver {

  private final MultiplayerLobbyScene multiplayerLobbyScene;
  private final SceneController sceneController;
  private UserDto myUser;
  private BorderPane root;
  private GameLobby gameLobby;


  public MultiplayerLobbySceneController(MultiplayerLobbyScene multiplayerLobbyScene) {
    this.multiplayerLobbyScene = multiplayerLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
    LobbyConfiguration.addGameLobbyObserver(this);
  }

  public void init() {
    this.gameLobby = LobbyConfiguration.getGameLobby();
    this.myUser = GameConfiguration.getMyGameUser();
    this.root = (BorderPane) multiplayerLobbyScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/Segoe UI Bold.ttf"), 26);
    // Initialize elements
    initHBox();
    initElements();
  }

  private void initElements() {
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> handleQuitGameLobby());

    // Initialize the username TextField
    Label title = new Label("Multiplayer Lobby");
    title.setAlignment(Pos.CENTER);
    title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 46px;");

    HBox titleBox = new HBox(title);
    titleBox.setAlignment(Pos.CENTER);

    Button play = new Button("Play");
    applyButtonStyle(play);
    play.setPrefWidth(470);
    play.setPrefHeight(40);
    play.setAlignment(Pos.CENTER);
    play.setFont(new Font(18));

    HBox playButton = new HBox(play);
    playButton.setAlignment(Pos.CENTER);
    playButton.setVisible(checkIfUserIsOwner());
    play.setOnMouseClicked(e -> handlePlayButton());

    //TODO: Implement Gridpane for Multiplayer Settings

    root.setBottom(playButton);
    root.setTop(titleBox);
    root.setLeft(backButton);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    BorderPane.setMargin(playButton, new Insets(10, 20, 20, 10));
    BorderPane.setMargin(titleBox, new Insets(10, 20, 20, 10));

  }

  private boolean checkIfUserIsOwner() {
    return myUser.getUsername().equals(gameLobby.getLobbyOwner().getUsername());
  }

  private void handleQuitGameLobby() {
    if (StyleConfiguration.showConfirmationDialog("Leave Lobby",
        "Are you sure that you want to leave the Lobby?")) {
      //gameLobby.removeUser(GameConfiguration.getMyGameUser());

      LobbyConfiguration.sendQuitGameLobby(GameConfiguration.getMyGameUser());
      sceneController.activate(SceneName.SELECT_LOBBY);
    }
  }

  private void initHBox() {
    System.out.println(gameLobby);
    HBox centralHBox = new HBox();
    List<UserDto> users = gameLobby.getUsers();
    for (UserDto user : users) {
      VBox userVBox = createPlayerVBox(user);
      centralHBox.getChildren().add(userVBox);
    }
    for (String bot : gameLobby.getBots()) {
      int i = 0;
      if (bot.contains("Medium")) {
        i = 1;
      }
      if (bot.contains("Hard")) {
        i = 2;
      }
      System.out.println(bot);
      VBox botVBox = createBotVBox(i, bot);
      centralHBox.getChildren().add(botVBox);
    }
    if (gameLobby.getBots().size() + gameLobby.getUsers().size() < gameLobby.getMaxPlayers()) {
      StackPane plus = createPlusStackpane();
      centralHBox.getChildren().add(plus);
    }
    centralHBox.setAlignment(Pos.CENTER);
    centralHBox.setSpacing(20.0);
    root.setCenter(centralHBox);

  }

  private void userClicked(UserDto user) {
    LobbyUserStatisticScene scene = (LobbyUserStatisticScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.LOBBY_USER_STATISTIC);
    if (scene == null) {
      scene = new LobbyUserStatisticScene();
      LobbyUserStatisticSceneController lobbyUserStatisticSceneController = new LobbyUserStatisticSceneController(
          scene);
      scene.setController(lobbyUserStatisticSceneController);
      sceneController.addScene(SceneName.LOBBY_USER_STATISTIC, scene);
    }
    pauseTitleSound();
    scene.setUserDto(user);
    sceneController.activate(SceneName.LOBBY_USER_STATISTIC);
  }


  private VBox createPlayerVBox(UserDto userDto) {
    //TODO: Image Path für UserDto nachfragen
    StackPane userImage = createPlayerStackPane("/com/unima/risk6/pictures/playerIcon.png"
        , false);
    Label userName = new Label(userDto.getUsername());
    userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;"
        + "-fx-background-color: #CCCCCC; -fx-border-color: #000000; -fx-border-radius: 20; "
        + "-fx-background-radius: 20; -fx-padding: 5 10 5 10; -fx-border-width: 2.0");
    VBox playerBox = new VBox(userImage, userName);
    playerBox.setOnMouseClicked(e -> userClicked(userDto));
    playerBox.setAlignment(Pos.CENTER);
    playerBox.setSpacing(-10);

    Button removeButton = new Button("");
    removeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent");

    VBox removeBox = new VBox(removeButton, playerBox);
    removeBox.setAlignment(Pos.CENTER);
    removeBox.setSpacing(15);

    return removeBox;
  }

  private StackPane createPlayerStackPane(String imagePath, boolean isBot) {
    Circle circle = new Circle();
    ImageView userImage = new ImageView(new Image(getClass().getResource(imagePath).toString()));
    if (isBot) {
      userImage.setFitHeight(130);
      userImage.setFitWidth(130);
      circle.setRadius(65);
    } else {
      userImage.setFitHeight(110);
      userImage.setFitWidth(110);
      circle.setRadius(70);
    }

    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(2.0);

    // create a clip for the user image
    Circle clip = new Circle(userImage.getFitWidth() / 2, userImage.getFitHeight() / 2,
        circle.getRadius());

    // apply the clip to the user image
    userImage.setClip(clip);

    // create a stack pane to place the circle and image on top of each other
    StackPane userStackPane = new StackPane();
    userStackPane.getChildren().addAll(circle, userImage);

    return userStackPane;
  }

  private StackPane createPlusStackpane() {
    ImageView plusImage = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/plusIcon.png").toString()));
    plusImage.setFitHeight(20);
    plusImage.setFitWidth(20);
    Circle circle = new Circle();
    circle.setRadius(20);
    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(2.0);

    Circle clip = new Circle(plusImage.getFitWidth() / 2, plusImage.getFitHeight() / 2,
        circle.getRadius());

    plusImage.setClip(clip);

    StackPane plusStackPane = new StackPane();
    plusStackPane.getChildren().addAll(circle, plusImage);
    plusStackPane.setOnMouseClicked(e -> handlePlusButton());
    return plusStackPane;
  }

  private void handlePlusButton() {
    if (checkIfUserIsOwner()) {
      if (gameLobby.getUsers().size() + gameLobby.getBots().size() < gameLobby.getMaxPlayers()) {
        botAdded();
      } else {
        showErrorDialog("Maximum number of players reached",
            "You can not add more players to this game lobby.");
      }
    } else {
      showMessage();
    }

  }

  private void botAdded() {
    // Erstellen Sie eine Liste der Auswahlmöglichkeiten
    List<String> choices = new ArrayList<>();
    choices.add("Easy");
    choices.add("Medium");
    choices.add("Hard");

    // Erstellen Sie einen ChoiceDialog
    ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Easy", choices);
    choiceDialog.setTitle("Choice");
    choiceDialog.setHeaderText("Please choose difficulty level");
    choiceDialog.setContentText("Difficulties:");

    // Zeigen Sie den Dialog und speichern Sie das Ergebnis in einer Optional-Variable
    Optional<String> result = choiceDialog.showAndWait();

    // Überprüfen Sie das Ergebnis und führen Sie entsprechende Aktionen durch
    result.ifPresent(selectedOption -> {
      // Führen Sie Aktionen basierend auf der ausgewählten Option durch
      int difficulty = switch (result.get()) {
        case "Medium" -> {
          MediumBot mediumBot = new MediumBot();
          gameLobby.getBots().add(mediumBot.getUser());
          yield 1;
        }
        case "Hard" -> {
          HardBot hardBot = new HardBot();
          gameLobby.getBots().add(hardBot.getUser());
          yield 2;
        }
        default -> {
          EasyBot easyBot = new EasyBot();
          gameLobby.getBots().add(easyBot.getUser());
          yield 0;
        }
      };
      LobbyConfiguration.sendBotJoinLobby(gameLobby);
    });
  }

  private VBox createBotVBox(int difficultyNumber, String botName) {
    StackPane botImage = new StackPane();
    switch (difficultyNumber) {
      case 0 -> botImage = createPlayerStackPane("/com/unima/risk6/pictures/easyBot.png", true);
      case 1 -> botImage = createPlayerStackPane("/com/unima/risk6/pictures/mediumBot.png", true);
      case 2 -> botImage = createPlayerStackPane("/com/unima/risk6/pictures/hardBot.png", true);
    }
    Label userName = new Label(botName);
    userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;"
        + "-fx-background-color: #CCCCCC; -fx-border-color: #000000; -fx-border-radius: 20; "
        + "-fx-background-radius: 20; -fx-padding: 5 10 5 10; -fx-border-width: 2.0");

    VBox botBox = new VBox(botImage, userName);
    botBox.setAlignment(Pos.CENTER);
    botBox.setSpacing(-10);

    Button removeButton = new Button("Remove");
    removeButton.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-font-size: 16; "
        + "-fx-background-color: lightgrey; -fx-border-color: black;");
    // removeButton.setOnMouseClicked(e -> removeBot(bot));
    VBox removeBox = new VBox(removeButton, botBox);
    removeBox.setAlignment(Pos.CENTER);
    removeBox.setSpacing(10);
    return removeBox;
  }

  private void showMessage() {
    showErrorDialog("Wait for other user to join", "You can not add players yourself. "
        + "You have to wait till other users join your game lobby.");
  }

  private void handlePlayButton() {

    int usersSize = gameLobby.getUsers().size();
    int together = usersSize + gameLobby.getBots().size();
    if (together < 2 || together > gameLobby.getMaxPlayers()) {
      showErrorDialog("Not enough players", "You need at least 2 players to start the game.");
      return;
    }

    LobbyConfiguration.sendStartGame(gameLobby);
  }

  @Override
  public void updateGameLobby(GameLobby gameLobby) {
    this.gameLobby = gameLobby;
    Platform.runLater(this::init);

  }

}

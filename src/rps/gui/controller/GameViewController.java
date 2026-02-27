package rps.gui.controller;

// Java imports
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.net.URL;
import java.util.ResourceBundle;

// Project imports
import rps.bll.game.GameManager;
import rps.bll.game.Move;
import rps.bll.game.Result;
import rps.bll.game.ResultType;
import rps.bll.player.Bot;
import rps.bll.player.Player;
import rps.bll.player.PlayerType;

public class GameViewController implements Initializable {

    @FXML
    private Label lblPlayerScore, lblBotScore, lblPlayerName, lblBotName;
    @FXML
    private ListView<String> lstHistory;

    private GameManager gameManager;
    private ObservableList<String> historyItems;
    private int humanWins = 0;
    private int botWins = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Player human = new Player("PLAYER", PlayerType.Human);
        Bot bot = new Bot("COMPUTER", PlayerType.AI);

        lblPlayerName.setText(human.getPlayerName());
        lblBotName.setText(bot.getPlayerName());

        gameManager = new GameManager(human, bot);

        historyItems = FXCollections.observableArrayList();
        lstHistory.setItems(historyItems);
    }

    @FXML
    private void handleMove(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();

        Move playerMove = switch (buttonId) {
            case "btnRock" -> Move.Rock;
            case "btnPaper" -> Move.Paper;
            case "btnScissor" -> Move.Scissor;
            default -> null;
        };
        if (playerMove == null) return;

        Result result = gameManager.playRound(playerMove);
        updateUI(result);
    }

    private void updateUI(Result result) {
        // determine the bot move regardless of outcome
        Move botMove = (result.getType() == ResultType.Tie)
                ? result.getWinnerMove()
                : (result.getWinnerPlayer().getPlayerType() == PlayerType.AI)
                ? result.getWinnerMove()
                : result.getLoserMove();

        String roundResultText;

        if (result.getType() == ResultType.Tie) {
            roundResultText = "Round " + result.getRoundNumber() + ": TIE! (Both played " + botMove + ")";
        } else if (result.getWinnerPlayer().getPlayerType() == PlayerType.Human) {
            humanWins++;
            lblPlayerScore.setText(String.valueOf(humanWins));
            roundResultText = "Round " + result.getRoundNumber() + ": YOU WON! (" + result.getWinnerMove() + " beats " + result.getLoserMove() + ")";
        } else {
            botWins++;
            lblBotScore.setText(String.valueOf(botWins));
            roundResultText = "Round " + result.getRoundNumber() + ": BOT WON! (" + result.getWinnerMove() + " beats " + result.getLoserMove() + ")";
        }

        historyItems.addFirst(roundResultText);
    }

    @FXML
    private void handleReset(ActionEvent event) {
        humanWins = 0;
        botWins = 0;
        lblPlayerScore.setText("0");
        lblBotScore.setText("0");
        historyItems.clear();

        Player human = new Player("PLAYER", PlayerType.Human);
        Bot bot = new Bot("COMPUTER", PlayerType.AI);
        gameManager = new GameManager(human, bot);
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }
}
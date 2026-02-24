package rps.gui.controller;

// Java imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
        Bot bot = new Bot("U_LOSE", PlayerType.AI);

        lblPlayerName.setText(human.getPlayerName());
        lblBotName.setText(bot.getPlayerName());

        gameManager = new GameManager(human, bot);

        historyItems = FXCollections.observableArrayList();
        lstHistory.setItems(historyItems);
    }

    /**
     * Handles the click event for Rock, Paper, and Scissor buttons.
     * All three buttons in FXML point to this method.
     */
    @FXML
    private void handleMove(ActionEvent event) {
        // Get the button that was clicked
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();

        // Map the button ID to a Move enum
        Move playerMove;
        if (buttonId.equals("btnRock")) {
            playerMove = Move.Rock;
        } else if (buttonId.equals("btnPaper")) {
            playerMove = Move.Paper;
        } else if (buttonId.equals("btnScissor")) {
            playerMove = Move.Scissor;
        } else {
            return; // Safety check
        }

        // Execute the round
        processRound(playerMove);
    }

    private void processRound(Move playerMove) {
        // Let the GameManager determine the outcome
        Result result = gameManager.playRound(playerMove);

        // Update scores and history
        updateUI(result);
    }

    private void updateUI(Result result) {
        String roundResultText = "";
        Move botMove = (result.getWinnerPlayer().getPlayerType() == PlayerType.AI)
                ? result.getWinnerMove()
                : result.getLoserMove();

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

        // Add result to the top of the history list
        historyItems.add(0, roundResultText);
    }

    @FXML
    private void handleReset(ActionEvent event) {
        // Reset scores
        humanWins = 0;
        botWins = 0;
        lblPlayerScore.setText("0");
        lblBotScore.setText("0");

        // Clear history
        historyItems.clear();

        // Re-initialize to reset Markov bot memory and round counter
        initialize(null, null);
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
}
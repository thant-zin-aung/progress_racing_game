package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.text.Format;

public class Controller {
    @FXML
    private AnchorPane screen;

    @FXML
    private JFXButton startButton;

    @FXML
    private JFXTextField p1Name;

    @FXML
    private JFXColorPicker p1Color;

    @FXML
    private JFXTextField p2Name;

    @FXML
    private JFXColorPicker p2Color;

    @FXML
    private ProgressBar p1Progress;

    @FXML
    private ProgressBar p2Progress;

    @FXML
    private Label p1Label;

    @FXML
    private Label p2Label;

    @FXML
    private Text goText;

    private int currentP1Progress;
    private int currentP2Progress;

    private KeyCode p1Key = KeyCode.A;
    private KeyCode p2Key = KeyCode.L;

    private int gameEndPoint = 70;

    private Task<ProgressBar> p1Task;
    private Task<ProgressBar> p2Task;

    private Thread p1Thread;
    private Thread p2Thread;

    private String winner;
    private boolean winnerFlag=false;

    @FXML
    public void initialize() {
        goText.setVisible(false);
        p1Color.setOnAction( e -> setProgressBarStyle(p1Progress,Integer.toHexString(p1Color.getValue().hashCode())));
        p2Color.setOnAction( e -> setProgressBarStyle(p2Progress,Integer.toHexString(p2Color.getValue().hashCode())));

        p1Task = new Task<ProgressBar>() {
            @Override
            protected ProgressBar call() throws Exception {
                while ( currentP1Progress <= gameEndPoint ) {
                    updateProgress(currentP1Progress,gameEndPoint);
                    if ( winnerFlag ) return p1Progress;
                }
                winner = p1Name.getText();
                winnerFlag=true;
                setLabelToVisible(p1Label,true);
                displayFinalStatus();
                return p1Progress;
            }
        };

        p2Task = new Task<ProgressBar>() {
            @Override
            protected ProgressBar call() throws Exception {
                while ( currentP2Progress <= gameEndPoint ) {
                    updateProgress(currentP2Progress,gameEndPoint);
                    if ( winnerFlag ) return p2Progress;
                }
                winner = p2Name.getText();
                winnerFlag=true;
                setLabelToVisible(p2Label,true);
                displayFinalStatus();
                return p2Progress;
            }
        };

        p1Progress.progressProperty().bind(p1Task.progressProperty());
        p2Progress.progressProperty().bind(p2Task.progressProperty());

        p1Thread = new Thread(p1Task);
        p2Thread = new Thread(p2Task);

    }

    public void pressStartButton() {
        goText.setVisible(true);
        new Thread(() -> {
            Platform.runLater( () -> {
                p1Label.setText(p1Name.getText());
                p2Label.setText(p2Name.getText());
            });
        }).start();
        p1Thread.start();
        p2Thread.start();
        screen.setOnKeyReleased( e -> {
            if ( e.getCode() == p1Key ) {
                ++currentP1Progress;
            } else if ( e.getCode() == p2Key ) {
                ++currentP2Progress;
            }
        });
    }

    private void setProgressBarStyle(ProgressBar progressBar,String color) {
        progressBar.setStyle("-fx-accent: #"+color);
    }

    private void displayFinalStatus() {
        Label finalWinnerLabel = (p1Label.isVisible())?p1Label:p2Label;
        new Thread( () -> Platform.runLater(() -> finalWinnerLabel.setText(winner+" is the winner"))).start();
        System.out.println("Winner " + winner);
        try {
            Media media = new Media("file:///" + System.getProperty("user.dir").replace('\\', '/') + "/src/" + "win.wav");
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    private void setLabelToVisible(Label label,boolean setVisible) {
        label.setVisible(setVisible);
    }

}

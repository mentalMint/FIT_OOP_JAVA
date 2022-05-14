package ru.nsu.fit.oop.tetris.View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import ru.nsu.fit.oop.tetris.Model;

import java.util.concurrent.Flow;

public class Score implements Flow.Subscriber<Boolean> {
    private final Model model;
    private final int height = 720;
    private final int width = 480;
    private final Pane layout = new Pane();
    private final Stage stage;
    private final Scene scene = new Scene(layout, width, height);
    private final Label score = new Label();

    public Score(Model model, Stage stage) {
        this.model = model;
        model.subscribe(this);
        this.stage = stage;

        layout.setBackground((new Background(new BackgroundFill(Color.DARKSLATEBLUE, CornerRadii.EMPTY, Insets.EMPTY))));

        TextField nameField = new TextField();
        int textFieldWidth = 250;
        int textFieldHeight = 70;
        nameField.setTranslateX((float) width / 2 - (float) textFieldWidth / 2);
        nameField.setTranslateY(200);
        nameField.setPrefWidth(textFieldWidth);
        nameField.setPrefHeight(textFieldHeight);
        nameField.setFont(Font.font("helvetica", FontWeight.BLACK, FontPosture.REGULAR, 20));
        nameField.setStyle("-fx-background-color: #8bbbb8");
        layout.getChildren().add(nameField);

        Button submitButton = ButtonCreator.createButton("Submit", 400);
        EventHandler<ActionEvent> submitEvent = e -> {
            try {
                String name = nameField.getText();
                model.addScore(name);
                model.toMenu();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        submitButton.setOnAction(submitEvent);
        layout.getChildren().add(submitButton);

        Button skipButton = ButtonCreator.createButton("Skip", 500);
        EventHandler<ActionEvent> skipEvent = e -> {
            try {
                model.toMenu();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        skipButton.setOnAction(skipEvent);
        layout.getChildren().add(skipButton);

        score.setText("Your score: " + model.getScore());
        score.setTranslateX((float) width / 2 - 100);
        score.setTranslateY(100);

        score.setFont(Font.font("helvetica", FontWeight.BLACK, FontPosture.REGULAR, 20));
        score.setStyle("-fx-text-fill: white;");
        layout.getChildren().add(score);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {

    }

    @Override
    public void onNext(Boolean item) {
        if (model.getGameState() == Model.GameState.SCORE) {
            score.setText("Your score: " + model.getScore());
            this.stage.setScene(scene);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}

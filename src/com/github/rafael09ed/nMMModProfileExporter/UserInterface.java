package com.github.rafael09ed.nMMModProfileExporter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;


/**
 * EGR 283 B01
 * UserInterface.java
 * Purpose:
 *
 * @author Rafael
 * @version 1.0 2/16/2017
 */
public class UserInterface extends Application {
    private final TextField pathField = new TextField();
    private final TextArea layoutArea = new TextArea(TextOutputFormater.DEMO_VALUE), modListArea = new TextArea();
    private final VBox modProfilesList = new VBox();
    private final Button autoFindButton = new Button("Auto"), demoButton = new Button("Demo"),
            markdownButton = new Button("Markdown"), copyButton = new Button("Copy Mod List");
    private ModProfile activeModProfile;

    public static void main(String[] args) {
        launch(args);
    }

    public UserInterface() {
        loadProfiles(PathFinder.findModProfiles());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox main = new HBox();
        VBox right = new VBox(), left = new VBox();
        Label label;

        label = new Label("Path:");
        pathField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadProfiles(PathFinder.findModProfiles(newValue));
        });
        HBox.setHgrow(pathField, Priority.SOMETIMES);
        autoFindButton.setOnAction(event -> loadProfiles(PathFinder.findModProfiles()));
        left.getChildren().addAll(label, new HBox(pathField, autoFindButton));

        label = new Label("Profiles:");
        modProfilesList.getStyleClass().add("profileList");
        ScrollPane scrollPane = new ScrollPane(modProfilesList);
        VBox.setVgrow(scrollPane, Priority.SOMETIMES);
        left.getChildren().addAll(label, scrollPane);

        label = new Label("Output Format:");
        layoutArea.textProperty().addListener((observable, oldValue, newValue) -> updateList());
        HBox.setHgrow(layoutArea, Priority.SOMETIMES);
        left.getChildren().addAll(label, layoutArea);

        label = new Label("Defaults:");
        demoButton.setOnAction(event -> {
            layoutArea.setText(TextOutputFormater.DEMO_VALUE);
            updateList();
        });
        markdownButton.setOnAction(event -> {
            layoutArea.setText(TextOutputFormater.MARKDOWN_VALUE);
            updateList();
        });
        left.getChildren().addAll(label, new HBox(demoButton, markdownButton));

        label = new Label("Mod List:");
        HBox.setHgrow(modListArea, Priority.SOMETIMES);
        VBox.setVgrow(modListArea, Priority.SOMETIMES);
        copyButton.setOnAction(event -> {
            StringSelection selection = new StringSelection(modListArea.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });
        copyButton.setMaxWidth(Double.MAX_VALUE);
        right.getChildren().addAll(label, modListArea, copyButton);

        HBox.setHgrow(right, Priority.SOMETIMES);
        main.getChildren().addAll(left, right);
        primaryStage.setScene(new Scene(main, 900, 800));
        primaryStage.getScene().getStylesheets().add("style.css");

        primaryStage.setTitle("Nexus Mod Manager Mod Profile Extractor");
        primaryStage.show();
    }

    private void updateList() {
        if (activeModProfile == null) {
            modListArea.setText("");
            return;
        }
        modListArea.setText(TextOutputFormater
                .makeTextOutput(
                        layoutArea.getText(),
                        activeModProfile
                ));
    }

    private void loadProfiles(List<ModProfile> profiles) {
        modProfilesList.getChildren().clear();
        if (profiles.size() > 0)
            activeModProfile = profiles.get(0);
        for (ModProfile profile : profiles) {
            VBox vBox = new VBox();
            vBox.setOnMouseClicked(event -> {
                activeModProfile = profile;
                updateList();
            });
            Label label;

            String profileName = profile.getProfileName();
            if (profileName == null || profileName.trim().length() <= 0)
                profileName = "Untitled Profile";
            label = new Label(profileName);
            label.getStyleClass().add("profileTitle");
            vBox.getChildren().add(label);

            label = new Label(profile.getGameName());
            vBox.getChildren().add(label);

            label = new Label(profile.getProfilePath());
            vBox.getChildren().add(label);

            label = new Label(profile.getMods().size() + " Mods");
            vBox.getChildren().add(label);

            vBox.getStyleClass().add("profileListItem");
            vBox.setMaxWidth(Double.MAX_VALUE);
            modProfilesList.getChildren().add(vBox);
        }
        updateList();
    }

}

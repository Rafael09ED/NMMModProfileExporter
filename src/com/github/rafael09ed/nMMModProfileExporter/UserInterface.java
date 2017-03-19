package com.github.rafael09ed.nMMModProfileExporter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


/**
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
    private final PreferencesIO preferences = new PreferencesIO();

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

        primaryStage.setTitle("Nexus Mod Manager Mod Profile Extractor By Rafael09ED");
        primaryStage.setOnCloseRequest(event -> preferences.saveToFile());
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
                        activeModProfile,
                        preferences
                ));
    }

    private void loadProfiles(List<ModProfile> profiles) {
        modProfilesList.getChildren().clear();
        if (profiles.size() > 0)
            activeModProfile = profiles.get(0);
        for (ModProfile profile : profiles) {
            VBox modProfileVBox = new VBox();
            modProfileVBox.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    activeModProfile = profile;
                    updateList();
                } else {
                    ContextMenu modProfileContextMenu = new ContextMenu();
                    MenuItem modProfileMenuItem = new MenuItem("Set URL Subpath for Game");
                    modProfileMenuItem.setOnAction(e -> {
                        Dialog<Pair<String, String>> dialog = new Dialog<>();
                        dialog.setTitle("Set URL Subpath for Game");
                        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

                        GridPane grid = new GridPane();

                        TextField gameField = new TextField();
                        gameField.setPromptText("Game Path");
                        gameField.setText(profile.getGameName().toLowerCase());
                        TextField urlField = new TextField();
                        urlField.setPromptText("Nexus Mod URL SubPath");
                        grid.add(new Label("Game Path:"), 0, 0);
                        grid.add(gameField, 0, 1);
                        grid.add(new Label("URL Path:"), 1, 0);
                        grid.add(urlField, 1, 1);

                        dialog.getDialogPane().setContent(grid);
                        Platform.runLater(urlField::requestFocus);
                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == ButtonType.APPLY) {
                                return new Pair<>(gameField.getText(), urlField.getText());
                            }
                            return null;
                        });

                        Optional<Pair<String, String>> result = dialog.showAndWait();
                        result.ifPresent(values -> {
                            preferences.setUrlForGamePath(values.getKey(), values.getValue());
                            updateList();
                        });
                    });
                    modProfileContextMenu.getItems().add(modProfileMenuItem);

                    modProfileMenuItem = new MenuItem("Open Profile Path");
                    modProfileMenuItem.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().open(new File(profile.getProfilePath()));
                        } catch (IOException e1) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Could Not Open Path");
                            alert.show();
                        }
                    });
                    modProfileContextMenu.getItems().add(modProfileMenuItem);

                    modProfileContextMenu.show(modProfileVBox, Side.BOTTOM, 0, 0);
                }
            });

            Label label;

            String profileName = profile.getProfileName();
            if (profileName == null || profileName.trim().length() <= 0)
                profileName = "Untitled Profile";
            label = new Label(profileName);
            label.getStyleClass().add("profileTitle");
            modProfileVBox.getChildren().add(label);

            label = new Label(profile.getGameName());
            modProfileVBox.getChildren().add(label);

            label = new Label(profile.getProfilePath());
            modProfileVBox.getChildren().add(label);

            label = new Label(profile.getMods().size() + " Mods");
            modProfileVBox.getChildren().add(label);

            modProfileVBox.getStyleClass().add("profileListItem");
            modProfileVBox.setMaxWidth(Double.MAX_VALUE);
            modProfilesList.getChildren().add(modProfileVBox);
        }
        updateList();
    }

}

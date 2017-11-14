package com.github.hussainderry.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Password input dialog
 * @author Hussain Al-Derry
 * */
public class PasswordDialog extends Dialog<String> {

    private static final String ACCEPT_BUTTON_TEXT = "Done";
    private static final String HINT = "Password";
    private static final String HEADER_TEXT = "Please enter your password.";
    private final PasswordField passwordField;

    public PasswordDialog() {
        // Setting dialog title and content message
        setTitle(HINT);
        setHeaderText(HEADER_TEXT);

        // Creating password field and setting hint
        passwordField = new PasswordField();
        passwordField.setPromptText(HINT);

        // Creating accept and cancel buttons
        ButtonType passwordButtonType = new ButtonType(ACCEPT_BUTTON_TEXT, ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(passwordButtonType, ButtonType.CANCEL);

        // Creating content
        HBox hBox = new HBox();
        hBox.getChildren().add(passwordField);
        hBox.setPadding(new Insets(20));
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        getDialogPane().setContent(hBox);

        Platform.runLater(passwordField::requestFocus);
        setResultConverter(dialogButton -> (dialogButton == passwordButtonType) ? passwordField.getText() : null);
    }
}
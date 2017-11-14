/*
 * Copyright 2017 Hussain Al-Derry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
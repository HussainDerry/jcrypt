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
package com.github.hussainderry.controller;

import com.github.hussainderry.model.Command;
import com.github.hussainderry.model.Mode;
import com.github.hussainderry.model.StringResources;
import com.github.hussainderry.task.AsyncDecryptionTask;
import com.github.hussainderry.task.AsyncEncryptionTask;
import com.github.hussainderry.ui.PasswordDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * The main controller
 * @author Hussain Al-Derry
 */
public class MainController {

    /* Constants */
    private static final String ENCRYPTED_FILE_SUFFIX = ".encrypted";

    /* UI elements */
    @FXML private Button mButtonEncrypt;
    @FXML private Button mButtonDecrypt;
    @FXML private Button mButtonAbout;
    @FXML private Button mButtonOpenFile;
    @FXML private Button mButtonAction;
    @FXML private TextField mTextFieldSourceFile;
    @FXML private TextField mTextFieldTargetFile;
    @FXML private ProgressBar mProgressBar;

    /* Logger */
    private final Logger mLogger;

    /* Used to indicate if a process is running */
    private boolean isRunning;

    /* Current working mode (encrypt/decrypt) */
    private Mode mCurrentMode;

    /* Source and target files */
    private File mSourceFile;
    private File mTargetFile;

    /* Stage reference */
    private Stage mStage;

    public MainController(){
        this.mLogger = Logger.getLogger(MainController.class.getName());
        this.mCurrentMode = Mode.ENCRYPT;
        this.isRunning = false;
    }

    /**
     * Handler method for the main menu's Encrypt button
     * Sets the mode to encrypt and resets the ui
     */
    @FXML
    private void onEncryptClicked(){
        this.mCurrentMode = Mode.ENCRYPT;
        mButtonAction.setText("Encrypt");
        resetUI();
    }

    /**
     * Handler method for the main menu's Decrypt button
     * Sets the mode to decrypt and resets the ui
     */
    @FXML
    private void onDecryptClicked(){
        this.mCurrentMode = Mode.DECRYPT;
        mButtonAction.setText("Decrypt");
        resetUI();
    }

    /**
     * Handler method for the Open File button.
     * Opens a {@link FileChooser} and sets the Files and File paths if a file is selected.
     */
    @FXML
    private void onOpenFileClicked(){
        FileChooser mFileChooser = new FileChooser();
        mFileChooser.setTitle("Select A File To Encrypt");
        this.mSourceFile = mFileChooser.showOpenDialog(mStage);

        if(mSourceFile != null){
            String filePath = mSourceFile.getAbsolutePath();
            String targetPath;

            String fileName = mSourceFile.getName().substring(0, mSourceFile.getName().indexOf('.'));
            if(mCurrentMode == Mode.ENCRYPT){
                targetPath = filePath.concat(ENCRYPTED_FILE_SUFFIX);
            }else{
                targetPath = filePath.substring(0, filePath.lastIndexOf('/') + 1).concat(String.format("%s_decrypted", fileName));
            }

            mTextFieldSourceFile.setText(filePath);
            mTextFieldTargetFile.setText(targetPath);
            this.mTargetFile = new File(targetPath);
        }else{
            showInfoDialog(StringResources.ERROR_NO_FILE);
        }
    }

    /**
     * Handler method for the Action button.
     * Executes the operation related to the current {@link Mode}
     */
    @FXML
    private void onActionClicked(){
        if(mSourceFile == null || mTargetFile == null){
            showInfoDialog(StringResources.ERROR_NO_FILE);
            return;
        }
        if(!mSourceFile.canRead()){
            showInfoDialog(StringResources.ERROR_READ_PERMISSION);
            return;
        }
        if(mTargetFile.exists() && !mTargetFile.canWrite()){
            showInfoDialog(StringResources.ERROR_WRITE_PERMISSION);
            return;
        }

        PasswordDialog mDialog = new PasswordDialog();
        Optional<String> password = mDialog.showAndWait();
        if(password.isPresent()){
            if(mCurrentMode == Mode.ENCRYPT){
                encrypt(password.get());
            }else if(mCurrentMode == Mode.DECRYPT){
                decrypt(password.get());
            }
        }else{
            showInfoDialog("Please Enter A Password!");
        }
    }

    /**
     * Displays about dialog.
     */
    @FXML
    private void onAboutClicked(){
        showInfoDialog(String.format("JCrypt\nVersion: %s\n%s\n%s",
                StringResources.VERSION, StringResources.DEVELOPER, StringResources.GITHUB_URL));
    }

    /**
     * Starts an {@link AsyncEncryptionTask} using the provided password and the preset source and target files.
     * @param password The password to use
     */
    private void encrypt(final String password){
        final long startTime = System.currentTimeMillis();
        AsyncEncryptionTask mTask = new AsyncEncryptionTask(password, mSourceFile, mTargetFile);
        mProgressBar.progressProperty().bind(mTask.progressProperty());
        mTask.messageProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case Command.STARTED:{
                    mLogger.info(String.format(StringResources.PROCESS_STARTED, "Encryption"));
                    this.isRunning = true;
                    lockUI(true);
                    break;
                }
                case Command.ERROR:{
                    mLogger.info(StringResources.ERROR);
                    resetUI();
                    showInfoDialog(StringResources.ERROR);
                    lockUI(false);
                    this.isRunning = false;
                    break;
                }
                case Command.FINISHED:{
                    resetUI();
                    final String message = String.format(StringResources.PROCESS_COMPLETE, (System.currentTimeMillis() - startTime));
                    mLogger.info(message);
                    showInfoDialog(message);
                    lockUI(false);
                    this.isRunning = false;
                    break;
                }
                default:{
                    mLogger.warning("Unknown Message");
                    break;
                }
            }
        });
        new Thread(mTask).start();
    }

    /**
     * Starts an {@link AsyncDecryptionTask} using the provided password and the preset source and target files.
     * @param password The password to use
     */
    private void decrypt(final String password){
        final long startTime = System.currentTimeMillis();
        AsyncDecryptionTask mTask = new AsyncDecryptionTask(password, mSourceFile, mTargetFile);
        mProgressBar.progressProperty().bind(mTask.progressProperty());
        mTask.messageProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case Command.STARTED:{
                    mLogger.info(String.format(StringResources.PROCESS_STARTED, "Decryption"));
                    this.isRunning = true;
                    lockUI(true);
                    break;
                }
                case Command.WRONG_PASSWORD:{
                    mLogger.info(StringResources.ERROR_INVALID_PASSWORD);
                    resetUI();
                    showInfoDialog(StringResources.ERROR_INVALID_PASSWORD);
                    lockUI(false);
                    this.isRunning = false;
                    break;
                }
                case Command.ERROR:{
                    resetUI();
                    showInfoDialog(StringResources.ERROR);
                    lockUI(false);
                    this.isRunning = false;
                    break;
                }
                case Command.FINISHED:{
                    resetUI();
                    final String message = String.format(StringResources.PROCESS_COMPLETE, (System.currentTimeMillis() - startTime));
                    mLogger.info(message);
                    showInfoDialog(message);
                    lockUI(false);
                    this.isRunning = false;
                    break;
                }
                default:{
                    mLogger.warning("Unknown Message");
                    break;
                }
            }
        });
        new Thread(mTask).start();
    }

    /**
     * Used to enable/disable UI elements
     * @param lock true to disable UI elements else false
     */
    private void lockUI(boolean lock){
        mLogger.info((lock) ? "Locking UI" : "Unlocking UI");
        mButtonAbout.setDisable(lock);
        mButtonEncrypt.setDisable(lock);
        mButtonDecrypt.setDisable(lock);
        mButtonAction.setDisable(lock);
        mButtonOpenFile.setDisable(lock);
    }

    /**
     * Resets the source/target files and the UI elements
     */
    private void resetUI(){
        mSourceFile = null;
        mTargetFile = null;
        mTextFieldTargetFile.setText("");
        mTextFieldSourceFile.setText("");
        mProgressBar.progressProperty().unbind();
        mProgressBar.setProgress(0);
    }

    /**
     * Shows an information dialog with the provided message.
     * @param message The message to display
     */
    private void showInfoDialog(final String message){
        Alert mAlert = new Alert(Alert.AlertType.INFORMATION);
        mAlert.setResizable(false);
        mAlert.setHeaderText(message);
        mAlert.show();
    }

    /**
     * Sets the window close listener to prevent exiting while a process is running
     */
    private void setWindowCloseListener(){
        this.mStage.getScene().getWindow().setOnCloseRequest(event -> {
            if(isRunning){
                mLogger.warning("Process is running, ignoring exit request ...");
                event.consume();
            }else{
                mLogger.info("Exiting ...");
                Platform.exit();
            }
        });
    }

    /**
     * Setter for stage reference
     * @param mStage
     */
    public void setStage(Stage mStage){
        this.mStage = mStage;
        setWindowCloseListener();
    }
}

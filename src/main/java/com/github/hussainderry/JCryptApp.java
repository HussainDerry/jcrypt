package com.github.hussainderry;

import com.github.hussainderry.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The app main class
 * @author Hussain Al-Derry
 */
public class JCryptApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Loading Main window layout
        FXMLLoader mLoader = new FXMLLoader(getClass().getClassLoader().getResource("main_view.fxml"));
        Parent mParent = mLoader.load();

        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(mParent));
        primaryStage.setTitle("JCrypt");
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.show();

        MainController mController = mLoader.getController();
        mController.setStage(primaryStage);

        Platform.setImplicitExit(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

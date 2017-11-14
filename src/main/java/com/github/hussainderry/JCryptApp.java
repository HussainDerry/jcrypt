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

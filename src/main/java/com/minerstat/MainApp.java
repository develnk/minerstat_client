package com.minerstat;

import com.minerstat.algorithm.AbstractFactory;
import com.minerstat.algorithm.Algorithm;
import com.minerstat.algorithm.MinerFactory;
import com.minerstat.controllers.PrimaryController;
import com.minerstat.settings.Settings;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.collections.ObservableMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

public class MainApp extends Application {

    public static Stage primaryStage;
    public static final String serverUrl = "http://localhost:8080/api/v1/";
    private PrimaryController primaryController;
    private static File dir = new File("src/main/");
    public static String currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
    public static void main(String[] args) {
        launch(args);
    }
    private Algorithm algorithm;

    @Override
    public void start(Stage pStage) {
        primaryStage = pStage;
        primaryStage.setTitle("MinerStat");
        initRootLayout();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(MainApp.dir, "resources/main.fxml").toURI().toURL());
            VBox rootLayout = loader.load();
            primaryController = loader.getController();
            primaryController.setCurrentApp(this);
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(we -> closeApp());
            choseAlgorithm();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alert for the close application.
     */
    public void closeApp() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Confirmation");
        alert.setHeaderText("Cancel Creation");
        alert.setContentText("Are you sure you want to cancel creation?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            primaryStage.close();
        }

        if(result.get() == ButtonType.CANCEL) {
            alert.close();
        }
    }

    @Override
    public void stop() throws Exception {
        saveSettings();
        algorithm.stopAlgorithm();
        super.stop();
    }

    /**
     * Saving settings to user location.
     */
    public void saveSettings() {
        ObservableMap<String, String> list = FXCollections.observableMap(new HashMap<String, String>());
        list.put("MinerType", primaryController.getSelectedMinerType().toString());
        list.put("DirectoryText", primaryController.getDirectoryText().toString());
        list.put("logsFlag", primaryController.getLogsFlag().toString());
        list.put("remotePort", primaryController.getRemotePort().toString());
        list.put("rigPaneVisible", String.valueOf(primaryController.rig_pane.isVisible()));
        Settings.getInstance().saveAllProperties(list);
    }

    private void choseAlgorithm() {
        AbstractFactory factoryMiner = new MinerFactory();
        int minerType = (int) primaryController.getSelectedMinerType();
        algorithm = factoryMiner.getMiner(minerType);
        algorithm.startAlgorithm();
    }

}

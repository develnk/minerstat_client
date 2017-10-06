package com.minerstat.controllers;

import java.io.File;
import javax.inject.Inject;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.fxml.FXML;
import org.controlsfx.control.action.ActionProxy;

import com.minerstat.MainApp;
import com.minerstat.settings.Settings;

public class PrimaryController {

    private MainApp app;

    @FXML private Button directoryButton;

    @FXML private TextField directoryText;

    @FXML private TextField remotePort;

    @FXML private ChoiceBox miner_type;

    @FXML private CheckBox logsFlag;

    @FXML private MenuItem exit;

    @FXML private MenuItem about;

    @FXML
    public void initialize() {
        directoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(MainApp.primaryStage);
                if(selectedDirectory == null) {
                    directoryText.setText("No Directory selected");
                } else {
                    directoryText.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });

        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("claymore's dual Ethereum");
        items.add("sgminer");
        items.add("cgminer");
        items.add("bfgminer");
        miner_type.setItems(items);

        int minerTypeValue = Integer.valueOf(Settings.getInstance().getProperties("MinerType"));
        miner_type.getSelectionModel().select(minerTypeValue);
        directoryText.setText(Settings.getInstance().getProperties("DirectoryText"));
        boolean logsFlagValue = Boolean.valueOf(Settings.getInstance().getProperties("logsFlag"));
        logsFlag.setSelected(logsFlagValue);
        remotePort.setText(Settings.getInstance().getProperties("remotePort"));
    }

    public Object getSelectedMinerType() {
        return miner_type.getSelectionModel().getSelectedIndex();
    }

    public Object getDirectoryText() {
        return directoryText.getText();
    }

    public Object getRemotePort() {
        return remotePort.getText();
    }

    public Object getLogsFlag() {
        return logsFlag.isSelected();
    }

    @ActionProxy(text="Exit", accelerator="alt+F4")
    public void exitActon() {
        app.closeApp();
    }

    @FXML
    private void aboutAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Gluon Desktop");
        alert.setHeaderText("About Gluon Desktop");
        alert.setContentText("This is a basic Gluon Desktop Application");
        alert.showAndWait();
    }

    public void setCurrentApp(MainApp app) {
        this.app = app;
    }

    public void postInit() {}

    public void dispose() {}
}

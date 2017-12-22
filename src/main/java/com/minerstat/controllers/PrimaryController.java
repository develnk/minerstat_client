package com.minerstat.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.inject.Inject;

import com.minerstat.entity.UserRig;
import com.minerstat.entity.Worker;
import com.minerstat.service.Common;
import com.minerstat.service.RigService;
import com.minerstat.service.WorkerService;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.fxml.FXML;
import org.controlsfx.control.action.ActionProxy;

import com.minerstat.MainApp;
import com.minerstat.settings.Settings;

public class PrimaryController {

    private MainApp app;

    private RigService rigService;

    private WorkerService workerService;

    private Common commonService;

    @FXML private Button directoryButton;

    @FXML private TextField directoryText;

    @FXML private TextField remotePort;

    @FXML private ChoiceBox miner_type;

    @FXML private CheckBox logsFlag;

    @FXML private MenuItem exit;

    @FXML private MenuItem about;

    @FXML private TextField user_name;

    @FXML private PasswordField password;

    @FXML public AnchorPane rig_pane;

    @FXML private Button server_auth;

    @FXML private Button save_settings;

    @FXML public ChoiceBox exist_rig;

    @FXML private ChoiceBox exist_worker;

    @FXML private Button create_rig_worker;

    @FXML private TextField new_rig_name;

    private List<UserRig> userRigs;

    private List<Worker> workerList;

    public PrimaryController() {
        rigService = new RigService();
        userRigs = new ArrayList<>();
        workerList = new ArrayList<>();
        commonService = new Common();
        workerService = new WorkerService();
    }

    @FXML
    public void initialize() {
        fillRigsFromFile();
        fillWorkerFromFile();

        // Set directory with log files.
        directoryButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(MainApp.primaryStage);
            if(selectedDirectory == null) {
                directoryText.setText("No Directory selected");
            } else {
                directoryText.setText(selectedDirectory.getAbsolutePath());
            }
        });

        visibleRigPane();

        // ChoiceBox Exist_Rig.
        defaultExistRig();
        exist_rig.setTooltip(new Tooltip("Select the Rig"));
        exist_rig.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() <= userRigs.size() - 1) {
                    Settings.getInstance().saveProperties("rigId", userRigs.get(newValue.intValue()).getRigId());
                }
            }
        });

        // ChoiceBox Exist_Worker.
        defaultExistWorker();
        exist_worker.setTooltip(new Tooltip("Select the Worker"));
        exist_worker.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() <= workerList.size() - 1) {
                    Settings.getInstance().saveProperties("workerId", workerList.get(newValue.intValue()).getWorkerId());
                }
            }
        });

        server_auth.setOnAction(event -> {
            if (!commonService.CheckAuthorizationSettings()) {
                Boolean auth = commonService.authorization(user_name.getText(), password.getText());
                if (auth) {
                    rig_pane.setVisible(true);
                    setRigs();
                    setWorkers();
                }
            }
        });

        create_rig_worker.setOnAction(event -> {

        });

        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Claymore's dual Ethereum");
        items.add("Sgminer");
        items.add("Cgminer");
        items.add("Bfgminer");
        miner_type.setItems(items);

        int minerTypeValue = Integer.valueOf(Settings.getInstance().getProperties("MinerType"));
        miner_type.getSelectionModel().select(minerTypeValue);
        directoryText.setText(Settings.getInstance().getProperties("DirectoryText"));
        boolean logsFlagValue = Boolean.valueOf(Settings.getInstance().getProperties("logsFlag"));
        logsFlag.setSelected(logsFlagValue);
        remotePort.setText(Settings.getInstance().getProperties("remotePort"));
        user_name.setText(Settings.getInstance().getProperties("user_name"));
        password.setText(Settings.getInstance().getProperties("password"));
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

    public TextField getUser_name() {
        return user_name;
    }

    public void setUser_name(TextField user_name) {
        this.user_name = user_name;
    }

    public PasswordField getPassword() {
        return password;
    }

    public void setPassword(PasswordField password) {
        this.password = password;
    }

    private void visibleRigPane() {
        boolean visible = Boolean.valueOf(Settings.getInstance().getProperties("rigPaneVisible"));
        rig_pane.setVisible(visible);

    }

    private void setRigs() {
        List<UserRig> userRigList = rigService.getUserRigs();
        fillRigs(userRigList);
    }

    private void setWorkers() {
        List<Worker> workerList = workerService.getUserWorkers();
        fillWorkers(workerList);
    }

    private void fillRigsFromFile() {
        List<UserRig> userRigList = rigService.readUserRigs();
        if (userRigList.size() != 0) {
            fillRigs(userRigList);
        }
    }

    private void fillWorkerFromFile() {
        List<Worker> workersList = workerService.readWorkers();
        if (workersList.size() != 0) {
            fillWorkers(workersList);
        }
    }

    private void fillRigs(List<UserRig> userRigList) {
        userRigs = userRigList;
        ObservableList<String> rigs = FXCollections.observableArrayList();
        userRigList.forEach(rig -> {
            if (rig.getName() != null) {
                rigs.add(rig.getName());
            }
            else {
                rigs.add(rig.getRigId());
            }
        });

        int current = exist_rig.getSelectionModel().getSelectedIndex();
        exist_rig.setItems(rigs);
        exist_rig.getSelectionModel().select(current);
    }

    private void fillWorkers(List<Worker> workerList) {
        this.workerList = workerList;
        ObservableList<String> workers = FXCollections.observableArrayList();
        workerList.forEach(worker -> {
            if (worker.getWorkerName() != null) {
                workers.add(worker.getWorkerName());
            }
            else {
                workers.add(worker.getWorkerId());
            }
        });

        int current = exist_worker.getSelectionModel().getSelectedIndex();
        exist_worker.setItems(workers);
        exist_worker.getSelectionModel().select(current);
    }

    private void defaultExistRig() {
        String rigId = Settings.getInstance().getProperties("rigId");
        if (!rigId.isEmpty()) {
            Predicate<UserRig> predicate = c-> c.getRigId().equals(rigId);
            UserRig  obj = userRigs.stream().filter(predicate).findFirst().get();
            int index = userRigs.indexOf(obj);
            exist_rig.getSelectionModel().select(index);
        }
    }

    private void defaultExistWorker() {
        String workerId = Settings.getInstance().getProperties("workerId");
        if (!workerId.isEmpty()) {
            Predicate<Worker> predicate = c-> c.getWorkerId().equals(workerId);
            Worker  obj = workerList.stream().filter(predicate).findFirst().get();
            int index = workerList.indexOf(obj);
            exist_worker.getSelectionModel().select(index);
        }
    }

}

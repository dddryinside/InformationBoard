package org.msolutions.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.msolutions.DTO.Event;
import org.msolutions.InformationBoard;
import org.msolutions.PageLoader;
import org.msolutions.service.AppSettingsManager;
import org.msolutions.service.EventsManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ImportContentController extends PageLoader {
    @FXML private ToggleGroup group;
    @FXML private RadioButton radioButton;
    @FXML private RadioButton radioButton2;

    @FXML private ComboBox<Event> eventsComboBox;
    @FXML private Button chooseEvent;

    @FXML private Label contentName;
    @FXML private Label showSettings;

    Settings settings = new Settings();
    List<File> selectedFiles = null;

    private final ObservableList<Event> eventsObservableList = FXCollections.observableArrayList();

    public void initialize() {
        List<Event> events = EventsManager.getAllEvents();
        eventsObservableList.addAll(events);

        eventsComboBox.setItems(eventsObservableList);

        radioButton.setToggleGroup(group);
        radioButton2.setToggleGroup(group);
        group.selectToggle(radioButton);

        eventsComboBox.setDisable(true);
        chooseEvent.setDisable(true);
        showSettings.setText("Показывать по умолчанию");
        settings.showSettings = 0;

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton selectedRadioButton = (RadioButton) newValue;

            if (selectedRadioButton == radioButton) {
                showSettings.setText("Показывать по умолчанию");
                eventsComboBox.setDisable(true);
                chooseEvent.setDisable(true);
                settings.showSettings = 0;
            } else {
                showSettings.setText(null);
                eventsComboBox.setDisable(false);
                chooseEvent.setDisable(false);
                settings.showSettings = -1;
            }
        });
    }

    @FXML
    public void importContent(ActionEvent event) {
        settings.contentName = null;
        contentName.setText(null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображения");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.gif");
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Видео", "*.mp4");
        fileChooser.getExtensionFilters().addAll(imageFilter, videoFilter);

        selectedFiles = fileChooser.showOpenMultipleDialog(PageLoader.primaryStage);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            if (selectedFiles.size() > 1) {
                contentName.setText("Множество файлов");
                settings.contentName = "Множество файлов";
            } else {
                contentName.setText(selectedFiles.get(0).getName());
                settings.contentName = selectedFiles.get(0).getName();
            }
        }
    }

    @FXML
    public void chooseEvent() {
        Event selectedItem = eventsComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            showSettings.setText(selectedItem.toString());
            settings.showSettings = selectedItem.getId();
        }
    }

    @FXML
    public void saveContent() {
        if (settings.showSettings == -1) {
            invalidInputNotification("Вы забыли выбрать событие, для показа контента!");
        } else if (settings.contentName == null) {
            invalidInputNotification("Вы забыли выбрать контент!");
        } else {
            try {
                File folder = findFolder(AppSettingsManager.getPath() + "/files", String.valueOf(settings.showSettings));

                for (File selectedFile : selectedFiles) {
                    Path source = Paths.get(selectedFile.getAbsolutePath());
                    Path target = Paths.get(folder.getAbsolutePath(), selectedFile.getName());
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }

                loadMainPage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File findFolder(String parentFolderPath, String folderName) {
        File parentFolder = new File(parentFolderPath);
        File[] files = parentFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().equalsIgnoreCase(folderName)) {
                    return file;
                }
            }
        }
        return null;
    }

    private void invalidInputNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    class Settings {
        public String contentName;
        public int showSettings;
    }
}

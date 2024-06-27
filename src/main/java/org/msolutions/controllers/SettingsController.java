package org.msolutions.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import org.msolutions.PageLoader;
import org.msolutions.service.AppSettingsManager;

import java.io.IOException;
import java.util.List;

public class SettingsController extends PageLoader {
    @FXML private CheckBox showTimeCheckBox;
    @FXML private CheckBox showWeatherCheckBox;
    @FXML private CheckBox showVideoWithSoundCheckBox;
    @FXML private CheckBox showDemonstrationInWindowCheckBox;
    @FXML private Spinner<Integer> slideDurationSpinner;

    @FXML
    public void initialize() {
        List<String> settings = AppSettingsManager.getSettings();

        slideDurationSpinner.getValueFactory().setValue(Integer.valueOf(settings.get(0)));

        if (settings.get(1).equals("true")) {
            showTimeCheckBox.setSelected(true);
        } else {
            showTimeCheckBox.setSelected(false);
        }

        if (settings.get(2).equals("true")) {
            showWeatherCheckBox.setSelected(true);
        } else {
            showWeatherCheckBox.setSelected(false);
        }

        if (settings.get(3).equals("true")) {
            showVideoWithSoundCheckBox.setSelected(true);
        } else {
            showVideoWithSoundCheckBox.setSelected(false);
        }

        if (settings.get(4).equals("true")) {
            showDemonstrationInWindowCheckBox.setSelected(true);
        } else {
            showDemonstrationInWindowCheckBox.setSelected(false);
        }
    }

    @FXML
    private void saveSettings() throws IOException {
        AppSettingsManager.updateSettings(slideDurationSpinner.getValueFactory().getValue(),
                showTimeCheckBox.isSelected(),
                showWeatherCheckBox.isSelected(),
                showVideoWithSoundCheckBox.isSelected(),
                showDemonstrationInWindowCheckBox.isSelected());

        loadMainPage();
    }
}

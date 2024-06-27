package org.msolutions.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.msolutions.DTO.Event;
import org.msolutions.service.AppSettingsManager;
import org.msolutions.service.EventsManager;
import org.msolutions.service.WeatherForecast;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DemonstrationController {
    @FXML private ImageView imageView;
    @FXML private MediaView mediaView;
    @FXML private StackPane mainPanel;
    @FXML private StackPane weatherTable;
    @FXML private Label weatherLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label windSpeedLabel;
    @FXML private Label pressureLabel;
    @FXML private Label timeLabel;

    private MediaPlayer mediaPlayer;
    private Timeline timeline;

    private int slideDuration;
    private boolean showVideoWithSound;
    private boolean showTime;
    private boolean showWeather;

    private int currentIndex = 0;
    private int showCounter = 0;
    private int prevShowIndex = 0;
    private List<File> content = new ArrayList<>();
    private static boolean freshContent = false;

    @FXML
    public void initialize() {
        refreshContent();

        Timer timer = new Timer();

        // Запускаем таймер, вызывающий функцию checkTime() каждую минуту
        timer.scheduleAtFixedRate(new CheckTimeTask(), 0, 60 * 1000);

        List<String> appSettings = AppSettingsManager.getSettings();
        slideDuration = Integer.parseInt(appSettings.get(0));

        if (appSettings.get(1).equals("true")) {
            showTime = true;
        } else {
            showTime = false;
        }

        if (appSettings.get(2).equals("true")) {
            showWeather = true;
        } else {
            showWeather = false;
        }

        if (appSettings.get(3).equals("true")) {
            showVideoWithSound = true;
        } else {
            showVideoWithSound = false;
        }

        weatherTable.setVisible(false);

        showNextMedia();
    }

    static class CheckTimeTask extends TimerTask {
        @Override
        public void run() {
            checkTime();
        }
    }

    public static void checkTime() {
        // Получаем текущее время
        LocalTime currentTime = LocalTime.now();

        // Проверяем, является ли время :00, :15, :30 или :45
        int minute = currentTime.getMinute();
        if (minute == 0 || minute == 15 || minute == 30 || minute == 45) {
            System.out.println("Время: " + currentTime.toString());
            // Добавьте здесь код, который нужно выполнять в это время
            freshContent = false;
        }
    }

    public List<File> getFilesInFolder(String name) {
        File directory = new File(AppSettingsManager.getPath() + "/files/" + name);
        File[] filesArray = directory.listFiles();
        List<File> fileList = new ArrayList<>();
        if (filesArray != null) {
            for (File file : filesArray) {
                if (file.isFile()) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    private void refreshContent() {
        content.clear();

        content.addAll(getFilesInFolder("0"));

        List<Event> eventList = EventsManager.getActualEvents();
        for (Event event : eventList) {
            content.addAll(getFilesInFolder(String.valueOf(event.getId())));
        }

        freshContent = true;
    }

    private void showNextMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        if (!freshContent) {
            refreshContent();
        }

        if (currentIndex < content.size()) {
            File file = content.get(currentIndex);
            if (showCounter == 5) {
                showCounter = -1;

                if (prevShowIndex == 1) {
                    prevShowIndex = 0;
                } else {
                    prevShowIndex = 1;
                }

                if (showTime && prevShowIndex == 1) {
                    System.out.println("Time next");
                    showTime();
                } else if (showWeather && prevShowIndex == 0) {
                    System.out.println("Weather next");
                    showWeather();
                } else {
                    showNextMedia();
                }

            } else if (isImageFile(file)) {
                System.out.println("Image next");
                showImage(file);
                currentIndex = (currentIndex + 1) % content.size();
            } else if (isVideoFile(file)) {
                System.out.println("Video next");
                showVideo(file);
                currentIndex = (currentIndex + 1) % content.size();
            }
            showCounter += 1;
        }
    }

    private boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png");
    }

    private boolean isVideoFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4");
    }

    private void showImage(File file) {
        Platform.runLater(() -> {

            imageView.setImage(new Image(file.toURI().toString()));

            imageView.setVisible(true);

            mediaView.setVisible(false);
            timeLabel.setVisible(false);
            weatherTable.setVisible(false);

            imageView.fitHeightProperty().bind(mainPanel.heightProperty());

            timeSleep();
        });
    }

    private void showVideo(File file) {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setMute(!showVideoWithSound);
            mediaView.setMediaPlayer(mediaPlayer);

            imageView.setVisible(false);
            timeLabel.setVisible(false);
            weatherTable.setVisible(false);
            mediaView.setVisible(true);

            mediaView.fitHeightProperty().bind(mainPanel.heightProperty());

            mediaPlayer.setOnReady(() -> {
                try {
                    mediaPlayer.play();
                } catch (Exception e) {
                    System.out.println("Error playing media: " + e.getMessage());
                    showNextMedia();
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Video finished");
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
                showNextMedia();
            });

            mediaPlayer.setOnError(() -> {
                System.out.println("Media error occurred: " + mediaPlayer.getError().getMessage());
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
                showNextMedia();
            });
        });
    }

    private void showTime() {
        Platform.runLater(() -> {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String currentTime = LocalTime.now().format(formatter);
            timeLabel.setText(currentTime);

            timeLabel.setVisible(true);

            imageView.setVisible(false);
            mediaView.setVisible(false);
            weatherTable.setVisible(false);

            timeSleep();
        });
    }

    private void showWeather() {
        Platform.runLater(() -> {

            List<String> weatherData = WeatherForecast.getWeather();

            weatherLabel.setText(weatherData.get(0));
            temperatureLabel.setText(weatherData.get(1) + "°C");
            windSpeedLabel.setText(weatherData.get(2) + "м/с");
            pressureLabel.setText(weatherData.get(3) + "%");

            imageView.setVisible(false);
            mediaView.setVisible(false);
            timeLabel.setVisible(false);

            weatherTable.setVisible(true);

            timeSleep();
        });
    }

    private void timeSleep() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(slideDuration), event -> showNextMedia()));
        timeline.play();
    }

    public void stopPlayback() {
        System.out.println("stop");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (timeline != null) {
            timeline.stop();
        }
    }
}

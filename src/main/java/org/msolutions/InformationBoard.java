package org.msolutions;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.msolutions.DTO.Event;
import org.msolutions.controllers.*;
import org.msolutions.service.AppSettingsManager;
import org.msolutions.service.EventsManager;
import org.msolutions.service.WeatherForecast;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * JavaFX InformationBoard
 */
public class InformationBoard extends Application {
    public static Stage demonstrationStage = null;

    @Override
    public void start(Stage primaryStage) throws IOException {
        startCheck();

        primaryStage.setTitle("Информационная панель");
        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        PageLoader.primaryStage = primaryStage;
        PageLoader.application = this;
        PageLoader pageLoader = new PageLoader();
        pageLoader.loadMainPage();
    }

    public void showDemonstration() throws IOException {
        if (demonstrationStage == null || !demonstrationStage.isShowing()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/demonstration.fxml"));
            Parent secondaryRoot = loader.load();
            secondaryRoot.setStyle("-fx-background-color: black;");

            demonstrationStage = new Stage();
            demonstrationStage.setScene(new Scene(secondaryRoot));
            demonstrationStage.setTitle("Демонстрация");
            Image icon = new Image("icon.png");
            demonstrationStage.getIcons().add(icon);

            if (AppSettingsManager.getSettings().get(4).equals("true")) {
                demonstrationStage.setFullScreen(false);
            } else {
                demonstrationStage.setFullScreen(true);
            }
            demonstrationStage.show();

            // Получаем контроллер для доступа к методу stopPlayback
            DemonstrationController controller = loader.getController();
            controller.demonstrationStage = demonstrationStage;

            demonstrationStage.setOnCloseRequest(event -> {
                controller.stopPlayback();
                demonstrationStage.close();
                demonstrationStage = null; // Сбрасываем ссылку на окно
            });

            // Добавляем обработчик нажатия клавиши ESCAPE для закрытия окна
            demonstrationStage.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    controller.stopPlayback();
                    demonstrationStage.close();
                    demonstrationStage = null; // Сбрасываем ссылку на окно
                }
            });
        } else {
            demonstrationStage.requestFocus(); // Активируем уже открытое окно
        }
    }

    public void startCheck() {
        String path = AppSettingsManager.getPath();

        // Проверяем, есть ли общая папка для сохранения файлов
        File directory = new File(path + "/files");
        if (directory.exists() && directory.isDirectory()) {
            System.out.println("Папка для сохранения файлов существует");
        } else {
            // Папки не существует
            directory.mkdir();
            System.out.println("Создана папка files");
        }

        // Проверяем, есть ли файл для хранения событий
        EventsManager.getEventsXMLFile();

        // Проверяем, есть ли папка для сохранения картинок по умолчанию
        File defaultFilesDirectory = new File(path + "/files/0");
        if (defaultFilesDirectory.exists() && defaultFilesDirectory.isDirectory()) {
            System.out.println("Папка для сохранения картинок по умолчанию существует");
        } else {
            // Папки не существует
            defaultFilesDirectory.mkdir();
            System.out.println("Создана папка 0");
        }

        //Обновление погоды
        WeatherForecast.weatherManager();

        //Удаление прошедших событий
        List<Event> eventList = EventsManager.getAllEvents();
        for (Event event : eventList) {
            if (event.getEndDate().isBefore(LocalDate.now())) {
                EventsManager.deleteEvent(event.getId());
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
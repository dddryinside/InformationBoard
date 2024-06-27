package org.msolutions.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import org.msolutions.DTO.Event;
import org.msolutions.PageLoader;
import org.msolutions.service.AppSettingsManager;
import org.msolutions.service.EventsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryController extends PageLoader {

    @FXML private VBox fileContainer;

    @FXML private ScrollPane scrollPane;


    // Скорость прокрутки
    private final double scrollSpeed = 0.005;

    List<File> content = new ArrayList<>();

    @FXML
    public void initialize() {
        Label loadingLabel = new Label("Загрузка...");
        loadingLabel.setPadding(new Insets(220, 0, 0, 0));
        fileContainer.getChildren().add(loadingLabel);

        Task<List<File>> loadFilesTask = new Task<>() {
            @Override
            protected List<File> call() {
                List<File> allFiles = new ArrayList<>();
                allFiles.addAll(loadFilesFromDirectory(AppSettingsManager.getPath() + "/files/0"));

                List<Event> events = EventsManager.getAllEvents();
                for (Event event : events) {
                    List<File> eventFiles = loadFilesFromDirectory(AppSettingsManager.getPath() + "/files/" + event.getId());
                    allFiles.addAll(eventFiles);
                }
                return allFiles;
            }
        };

        loadFilesTask.setOnSucceeded(event -> {
            content.addAll(loadFilesTask.getValue());

            for (File file : content) {
                VBox fileBox = createFileBox(file);
                fileContainer.getChildren().add(fileBox);
            }

            if (content.size() == 0) {
                Label noContentMessage = new Label("Пока что тут ничего нет... Добавьте контент!");
                noContentMessage.setPadding(new Insets(220, 0, 0, 0));
                fileContainer.getChildren().add(noContentMessage);
            }

            fileContainer.getChildren().remove(loadingLabel);
        });

        loadFilesTask.setOnFailed(event -> {
            fileContainer.getChildren().remove(loadingLabel);
            Label errorMessage = new Label("Ошибка при загрузке файлов.");
            fileContainer.getChildren().add(errorMessage);
        });

        new Thread(loadFilesTask).start();

        // Обработчик для изменения скорости прокрутки
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY() * scrollSpeed;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
            event.consume();
        });
    }

    private List<File> loadFilesFromDirectory(String path) {
        File directory = new File(path);
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

    private VBox createFileBox(File file) {
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        if (isImageFile(file)) {
            ImageView imageView = new ImageView(new Image(file.toURI().toString()));
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(200);
            vBox.getChildren().add(imageView);
        } else if (isVideoFile(file)) {
            ImageView thumbnail = createVideoThumbnail(file);
            thumbnail.setFitHeight(200);
            vBox.getChildren().add(thumbnail);
        }

        Label contentName = new Label(file.getName());
        vBox.getChildren().add(contentName);

        Button deleteButton = new Button("Удалить");
        deleteButton.setPrefWidth(220);
        deleteButton.setPrefHeight(40);
        deleteButton.setOnAction(event -> {
            deleteFile(file);
            fileContainer.getChildren().remove(vBox);
        });

        vBox.getChildren().add(deleteButton);
        return vBox;
    }

    private boolean isImageFile(File file) {
        String lowerCaseName = file.getName().toLowerCase();
        return lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg") || lowerCaseName.endsWith(".gif");
    }

    private boolean isVideoFile(File file) {
        String lowerCaseName = file.getName().toLowerCase();
        return lowerCaseName.endsWith(".mp4");
    }

    // Использование заготовленной картинки для отображения заставки видео
    private ImageView createVideoThumbnail(File file) {
        Image thumbnailImage = new Image("play-button.png");
        ImageView thumbnail = new ImageView(thumbnailImage);
        thumbnail.setPreserveRatio(true);

        return thumbnail;
    }

    private void deleteFile(File file) {
        if (file.delete()) {
            System.out.println("Файл удалён: " + file.getName());
        } else {
            System.out.println("Не удалось удалить файл: " + file.getName());
        }
    }
}

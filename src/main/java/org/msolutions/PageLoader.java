package org.msolutions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class PageLoader {
    public static Stage primaryStage;
    public static InformationBoard application;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 700;

    public void loadMainPage() {
        loadPage("/view/main.fxml");
    }

    public void loadSettingsPage() {
        loadPage("/view/settings.fxml");
    }

    public void loadImportContentPage() {
        loadPage("/view/import-content.fxml");
    }

    public void loadGalleryPage() {
        loadPage("/view/gallery.fxml");
    }

    public void loadAddEventPage() {
        loadPage("/view/add-event.fxml");
    }

    public void loadDeleteEventsPage() {
        loadPage("/view/delete-event.fxml");
    }

    public void loadAboutAppPage() {
        loadPage("/view/about-app.fxml");
    }

    private void loadPage(String FXMLFileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(InformationBoard.class.getResource(FXMLFileName));
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            errorNotification();
        }
    }

    private void errorNotification() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText("Ошибка загрузки страницы!");
        alert.showAndWait();
    }
}

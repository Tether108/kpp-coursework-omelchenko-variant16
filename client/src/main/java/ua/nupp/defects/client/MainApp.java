package ua.nupp.defects.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import ua.nupp.defects.client.ui.DefectsTab;
import ua.nupp.defects.client.ui.InspectionsTab;
import ua.nupp.defects.client.ui.ObjectsTab;
import ua.nupp.defects.client.ui.TasksTab;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        ApiClient api = new ApiClient();
        ObjectsTab objectsTab = new ObjectsTab(api);
        TabPane tabs = new TabPane(
                objectsTab,
                new InspectionsTab(api),
                new DefectsTab(api),
                new TasksTab(api));

        stage.setScene(new Scene(tabs, 1260, 660));
        stage.setTitle("Облік дефектів будівельних об'єктів");
        stage.show();
        objectsTab.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

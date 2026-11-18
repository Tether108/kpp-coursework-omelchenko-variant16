package ua.nupp.defects.client.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ua.nupp.defects.client.ApiClient;
import ua.nupp.defects.client.ImportInspectionsTask;
import ua.nupp.defects.client.model.Inspection;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

public class InspectionsTab extends Tab {
    private static final Logger LOG = Logger.getLogger(InspectionsTab.class.getName());

    private final ApiClient api;
    private final TableView<Inspection> table = new TableView<>();
    private final TextField objectId = new TextField();
    private final TextField date = new TextField();
    private final TextField inspector = new TextField();
    private final TextField category = new TextField();
    private final TextField notes = new TextField();

    private final Button importButton = new Button("Імпорт з CSV...");
    private final ProgressBar progress = new ProgressBar(0);
    private final Label importStatus = new Label("Імпорт не запускався");

    public InspectionsTab(ApiClient api) {
        super("Обстеження");
        this.api = api;
        setClosable(false);

        table.getColumns().add(Ui.column("ID", "id", 50));
        table.getColumns().add(Ui.column("ID об'єкта", "objectId", 90));
        table.getColumns().add(Ui.column("Дата", "inspectionDate", 100));
        table.getColumns().add(Ui.column("Інспектор", "inspector", 160));
        table.getColumns().add(Ui.column("Категорія стану", "conditionCategory", 130));
        table.getColumns().add(Ui.column("Примітка", "notes", 300));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, i) -> {
            if (i != null) {
                objectId.setText(String.valueOf(i.getObjectId()));
                date.setText(i.getInspectionDate());
                inspector.setText(i.getInspector());
                category.setText(String.valueOf(i.getConditionCategory()));
                notes.setText(i.getNotes());
            }
        });
        date.setPromptText("2026-11-17");
        category.setPromptText("1 - 4");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button reload = new Button("Оновити");

        add.setOnAction(e -> Ui.run(() -> {
            api.addInspection(readForm(null));
            refresh();
        }));
        update.setOnAction(e -> Ui.run(() -> {
            Inspection sel = Ui.selected(table);
            api.updateInspection(readForm(sel.getId()));
            refresh();
        }));
        delete.setOnAction(e -> Ui.run(() -> {
            api.deleteInspection(Ui.selected(table).getId());
            refresh();
        }));
        reload.setOnAction(e -> refresh());
        importButton.setOnAction(e -> chooseFileAndImport());

        progress.setMaxWidth(Double.MAX_VALUE);
        importStatus.setWrapText(true);

        VBox form = new VBox(8,
                Ui.field("ID будівельного об'єкта", objectId),
                Ui.field("Дата обстеження (РРРР-ММ-ДД)", date),
                Ui.field("Інспектор", inspector),
                Ui.field("Категорія технічного стану (1-4)", category),
                Ui.field("Примітка", notes),
                new HBox(6, add, update),
                new HBox(6, delete, reload),
                new Separator(),
                new Label("Імпорт результатів обстежень"),
                importButton,
                progress,
                importStatus);
        form.setPadding(new Insets(0, 0, 0, 10));
        form.setPrefWidth(270);

        BorderPane root = new BorderPane(table);
        root.setRight(form);
        root.setPadding(new Insets(10));
        setContent(root);

        setOnSelectionChanged(e -> {
            if (isSelected()) {
                refresh();
            }
        });
    }

    public void refresh() {
        Ui.run(() -> table.setItems(FXCollections.observableArrayList(api.getInspections())));
    }

    private Inspection readForm(Long id) {
        return new Inspection(id,
                Ui.toLong(objectId, "ID будівельного об'єкта"),
                Ui.text(date, "Дата обстеження"),
                Ui.text(inspector, "Інспектор"),
                Ui.toInt(category, "Категорія технічного стану"),
                notes.getText().trim());
    }

    private void chooseFileAndImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Виберіть CSV-файл з обстеженнями");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файли", "*.csv"));
        File dir = new File("sample-data");
        if (dir.isDirectory()) {
            chooser.setInitialDirectory(dir.getAbsoluteFile());
        }
        File file = chooser.showOpenDialog(getTabPane().getScene().getWindow());
        if (file != null) {
            startImport(file.toPath());
        }
    }

    private void startImport(Path path) {
        ImportInspectionsTask task = new ImportInspectionsTask(path, api);
        progress.progressProperty().bind(task.progressProperty());
        importStatus.textProperty().bind(task.messageProperty());
        importButton.setDisable(true);

        task.setOnSucceeded(e -> {
            unbind();
            if (task.getValue() == 0 && task.getSkipped() == 0) {
                importStatus.setText("Немає рядків для імпорту");
            } else {
                importStatus.setText("Готово. Імпортовано: " + task.getValue() + ", пропущено рядків: " + task.getSkipped());
            }
            refresh();
        });
        task.setOnFailed(e -> {
            unbind();
            Throwable ex = task.getException();
            String message = ex == null ? "Невідома помилка" : ex.getMessage();
            LOG.warning("Імпорт завершився з помилкою: " + message);
            importStatus.setText("Помилка імпорту: " + message);
            refresh();
            Ui.error(message);
        });

        Thread thread = new Thread(task, "csv-import");
        thread.setDaemon(true);
        thread.start();
    }

    private void unbind() {
        progress.progressProperty().unbind();
        importStatus.textProperty().unbind();
        importButton.setDisable(false);
    }
}

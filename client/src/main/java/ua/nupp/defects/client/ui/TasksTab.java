package ua.nupp.defects.client.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ua.nupp.defects.client.ApiClient;
import ua.nupp.defects.client.model.RepairTask;

public class TasksTab extends Tab {
    private final ApiClient api;
    private final TableView<RepairTask> table = new TableView<>();
    private final TextField defectId = new TextField();
    private final TextField description = new TextField();
    private final TextField executor = new TextField();
    private final TextField plannedDate = new TextField();

    public TasksTab(ApiClient api) {
        super("Ремонтні завдання");
        this.api = api;
        setClosable(false);

        table.getColumns().add(Ui.column("ID", "id", 50));
        table.getColumns().add(Ui.column("ID дефекту", "defectId", 80));
        table.getColumns().add(Ui.column("Опис робіт", "description", 320));
        table.getColumns().add(Ui.column("Виконавець", "executor", 180));
        table.getColumns().add(Ui.column("Планова дата", "plannedDate", 110));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, t) -> {
            if (t != null) {
                defectId.setText(String.valueOf(t.getDefectId()));
                description.setText(t.getDescription());
                executor.setText(t.getExecutor());
                plannedDate.setText(t.getPlannedDate());
            }
        });
        plannedDate.setPromptText("2026-12-01");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button reload = new Button("Оновити");

        add.setOnAction(e -> Ui.run(() -> {
            api.addTask(readForm(null));
            refresh();
        }));
        update.setOnAction(e -> Ui.run(() -> {
            RepairTask sel = Ui.selected(table);
            api.updateTask(readForm(sel.getId()));
            refresh();
        }));
        delete.setOnAction(e -> Ui.run(() -> {
            api.deleteTask(Ui.selected(table).getId());
            refresh();
        }));
        reload.setOnAction(e -> refresh());

        VBox form = new VBox(8,
                Ui.field("ID дефекту", defectId),
                Ui.field("Опис робіт", description),
                Ui.field("Виконавець", executor),
                Ui.field("Планова дата (РРРР-ММ-ДД)", plannedDate),
                new HBox(6, add, update),
                new HBox(6, delete, reload));
        form.setPadding(new Insets(0, 0, 0, 10));
        form.setPrefWidth(260);

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
        Ui.run(() -> table.setItems(FXCollections.observableArrayList(api.getTasks())));
    }

    private RepairTask readForm(Long id) {
        return new RepairTask(id,
                Ui.toLong(defectId, "ID дефекту"),
                Ui.text(description, "Опис робіт"),
                Ui.text(executor, "Виконавець"),
                Ui.text(plannedDate, "Планова дата"));
    }
}

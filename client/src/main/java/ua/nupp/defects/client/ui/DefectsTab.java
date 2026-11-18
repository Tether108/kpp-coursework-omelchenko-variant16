package ua.nupp.defects.client.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ua.nupp.defects.client.ApiClient;
import ua.nupp.defects.client.model.Defect;
import ua.nupp.defects.client.model.DefectState;

public class DefectsTab extends Tab {
    private final ApiClient api;
    private final TableView<Defect> table = new TableView<>();
    private final TextField inspectionId = new TextField();
    private final TextField description = new TextField();
    private final TextField severity = new TextField();
    private final TextField area = new TextField();
    private final TextField safety = new TextField();
    private final ComboBox<DefectState> newState = new ComboBox<>();

    public DefectsTab(ApiClient api) {
        super("Дефекти");
        this.api = api;
        setClosable(false);

        table.getColumns().add(Ui.column("ID", "id", 45));
        table.getColumns().add(Ui.column("Обстеження", "inspectionId", 85));
        table.getColumns().add(Ui.column("Опис", "description", 230));
        table.getColumns().add(Ui.column("Тяжкість", "severity", 72));
        table.getColumns().add(Ui.column("Площа, м2", "area", 78));
        table.getColumns().add(Ui.column("Безпека", "safetyScore", 68));
        table.getColumns().add(Ui.column("Стан", "state", 145));
        table.getColumns().add(Ui.column("F1", "f1", 48));
        table.getColumns().add(Ui.column("F2", "f2", 48));
        table.getColumns().add(Ui.column("F3", "f3", 48));
        table.getColumns().add(Ui.column("I", "riskIndex", 55));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, d) -> {
            if (d != null) {
                inspectionId.setText(String.valueOf(d.getInspectionId()));
                description.setText(d.getDescription());
                severity.setText(String.valueOf(d.getSeverity()));
                area.setText(String.valueOf(d.getArea()));
                safety.setText(String.valueOf(d.getSafetyScore()));
            }
        });
        severity.setPromptText("1 - 5");
        safety.setPromptText("0 - 100");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button reload = new Button("Оновити");

        add.setOnAction(e -> Ui.run(() -> {
            api.addDefect(readForm(null));
            refresh();
        }));
        update.setOnAction(e -> Ui.run(() -> {
            Defect sel = Ui.selected(table);
            api.updateDefect(readForm(sel.getId()));
            refresh();
        }));
        delete.setOnAction(e -> Ui.run(() -> {
            api.deleteDefect(Ui.selected(table).getId());
            refresh();
        }));
        reload.setOnAction(e -> refresh());

        newState.setItems(FXCollections.observableArrayList(DefectState.values()));
        newState.setMaxWidth(Double.MAX_VALUE);
        Button changeState = new Button("Змінити стан");
        changeState.setOnAction(e -> Ui.run(() -> {
            Defect sel = Ui.selected(table);
            if (newState.getValue() == null) {
                throw new IllegalArgumentException("Виберіть новий стан");
            }
            api.changeDefectState(sel.getId(), newState.getValue());
            refresh();
        }));

        VBox form = new VBox(8,
                Ui.field("ID обстеження", inspectionId),
                Ui.field("Опис дефекту", description),
                Ui.field("Тяжкість (1-5)", severity),
                Ui.field("Площа, м2", area),
                Ui.field("Вплив на безпеку (0-100)", safety),
                new HBox(6, add, update),
                new HBox(6, delete, reload),
                new Separator(),
                Ui.field("Новий стан", newState),
                changeState);
        form.setPadding(new Insets(0, 0, 0, 10));
        form.setPrefWidth(250);

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
        Ui.run(() -> table.setItems(FXCollections.observableArrayList(api.getDefects())));
    }

    private Defect readForm(Long id) {
        Defect d = new Defect();
        d.setId(id);
        d.setInspectionId(Ui.toLong(inspectionId, "ID обстеження"));
        d.setDescription(Ui.text(description, "Опис дефекту"));
        d.setSeverity(Ui.toInt(severity, "Тяжкість"));
        d.setArea(Ui.toDouble(area, "Площа"));
        d.setSafetyScore(Ui.toInt(safety, "Вплив на безпеку"));
        if (d.getSeverity() < 1 || d.getSeverity() > 5) {
            throw new IllegalArgumentException("Тяжкість має бути від 1 до 5");
        }
        if (d.getSafetyScore() < 0 || d.getSafetyScore() > 100) {
            throw new IllegalArgumentException("Вплив на безпеку має бути від 0 до 100");
        }
        return d;
    }
}

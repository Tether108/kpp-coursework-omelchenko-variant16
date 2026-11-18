package ua.nupp.defects.client.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ua.nupp.defects.client.ApiClient;
import ua.nupp.defects.client.model.BuildingObject;

public class ObjectsTab extends Tab {
    private final ApiClient api;
    private final TableView<BuildingObject> table = new TableView<>();
    private final TextField name = new TextField();
    private final TextField address = new TextField();
    private final TextField type = new TextField();
    private final TextField year = new TextField();

    public ObjectsTab(ApiClient api) {
        super("Будівельні об'єкти");
        this.api = api;
        setClosable(false);

        table.getColumns().add(Ui.column("ID", "id", 50));
        table.getColumns().add(Ui.column("Назва", "name", 220));
        table.getColumns().add(Ui.column("Адреса", "address", 260));
        table.getColumns().add(Ui.column("Тип", "objectType", 170));
        table.getColumns().add(Ui.column("Рік побудови", "buildYear", 110));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, o) -> {
            if (o != null) {
                name.setText(o.getName());
                address.setText(o.getAddress());
                type.setText(o.getObjectType());
                year.setText(String.valueOf(o.getBuildYear()));
            }
        });

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button reload = new Button("Оновити");

        add.setOnAction(e -> Ui.run(() -> {
            api.addObject(readForm(null));
            refresh();
            clearForm();
        }));
        update.setOnAction(e -> Ui.run(() -> {
            BuildingObject sel = Ui.selected(table);
            api.updateObject(readForm(sel.getId()));
            refresh();
        }));
        delete.setOnAction(e -> Ui.run(() -> {
            BuildingObject sel = Ui.selected(table);
            api.deleteObject(sel.getId());
            refresh();
            clearForm();
        }));
        reload.setOnAction(e -> refresh());

        VBox form = new VBox(8,
                Ui.field("Назва", name),
                Ui.field("Адреса", address),
                Ui.field("Тип об'єкта", type),
                Ui.field("Рік побудови", year),
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
        Ui.run(() -> table.setItems(FXCollections.observableArrayList(api.getObjects())));
    }

    private BuildingObject readForm(Long id) {
        return new BuildingObject(id,
                Ui.text(name, "Назва"),
                Ui.text(address, "Адреса"),
                Ui.text(type, "Тип об'єкта"),
                Ui.toInt(year, "Рік побудови"));
    }

    private void clearForm() {
        name.clear();
        address.clear();
        type.clear();
        year.clear();
    }
}

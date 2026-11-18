package ua.nupp.defects.client.ui;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

// допоміжні методи для вкладок
public class Ui {
    private static final Logger LOG = Logger.getLogger(Ui.class.getName());

    public static <S, T> TableColumn<S, T> column(String title, String property, double width) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    public static VBox field(String label, Control control) {
        control.setMaxWidth(Double.MAX_VALUE);
        return new VBox(2, new Label(label), control);
    }

    public static String text(TextField f, String name) {
        String value = f.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Заповніть поле \"" + name + "\"");
        }
        return value;
    }

    public static int toInt(TextField f, String name) {
        try {
            return Integer.parseInt(text(f, name));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле \"" + name + "\" має бути цілим числом");
        }
    }

    public static long toLong(TextField f, String name) {
        try {
            return Long.parseLong(text(f, name));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле \"" + name + "\" має бути цілим числом");
        }
    }

    public static double toDouble(TextField f, String name) {
        try {
            return Double.parseDouble(text(f, name));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле \"" + name + "\" має бути числом");
        }
    }

    public static <T> T selected(TableView<T> table) {
        T item = table.getSelectionModel().getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Спочатку виберіть запис у таблиці");
        }
        return item;
    }

    // виконує дію і показує помилку, якщо щось пішло не так
    public static void run(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            warning(e.getMessage());
        } catch (RuntimeException e) {
            LOG.warning("Помилка запиту: " + e.getMessage());
            error(e.getMessage());
        }
    }

    public static void error(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.setHeaderText("Помилка");
        a.showAndWait();
    }

    public static void warning(String message) {
        Alert a = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        a.setHeaderText("Перевірте дані");
        a.showAndWait();
    }
}

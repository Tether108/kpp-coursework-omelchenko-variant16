package ua.nupp.defects.client;

import javafx.concurrent.Task;
import ua.nupp.defects.client.model.Inspection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

// фоновий імпорт результатів обстежень з CSV
// рядок з помилкою формату пропускається, помилка сервера зупиняє імпорт
public class ImportInspectionsTask extends Task<Integer> {
    public static final String HEADER = "object_id;inspection_date;inspector;condition_category;notes";
    private static final Logger LOG = Logger.getLogger(ImportInspectionsTask.class.getName());

    private final Path file;
    private final ApiClient api;
    private int skipped = 0;

    public ImportInspectionsTask(Path file, ApiClient api) {
        this.file = file;
        this.api = api;
    }

    public int getSkipped() {
        return skipped;
    }

    @Override
    protected Integer call() throws Exception {
        LOG.info("Початок CSV-імпорту: " + file.getFileName());
        if (!Files.exists(file)) {
            throw new IOException("Файл не знайдено: " + file.getFileName());
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !HEADER.equals(lines.get(0).trim())) {
            throw new IOException("Неправильний заголовок CSV. Має бути: " + HEADER);
        }

        int total = lines.size() - 1;
        if (total == 0) {
            updateMessage("Немає рядків для імпорту");
            return 0;
        }

        int imported = 0;
        int processed = 0;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;
            if (!line.isBlank()) {
                Inspection inspection = null;
                try {
                    inspection = parseLine(line);
                } catch (IllegalArgumentException e) {
                    skipped++;
                    LOG.warning("Рядок " + lineNumber + " пропущено: " + e.getMessage());
                }
                if (inspection != null) {
                    try {
                        api.addInspection(inspection);
                    } catch (RuntimeException e) {
                        LOG.severe("Імпорт зупинено на рядку " + lineNumber + ": " + e.getMessage());
                        throw new RuntimeException("Рядок " + lineNumber + ": " + e.getMessage()
                                + ". Імпорт зупинено, до цього додано записів: " + imported);
                    }
                    imported++;
                }
            }
            processed++;
            updateProgress(processed, total);
            updateMessage("Опрацьовано " + processed + " із " + total
                    + ", додано: " + imported + ", пропущено: " + skipped);
        }

        LOG.info("CSV-імпорт завершено. Додано: " + imported + ", пропущено: " + skipped);
        return imported;
    }

    public static Inspection parseLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("має бути 5 полів, а знайдено " + parts.length);
        }
        long objectId;
        int category;
        try {
            objectId = Long.parseLong(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("object_id має бути числом");
        }
        try {
            LocalDate.parse(parts[1].trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("inspection_date має бути у форматі РРРР-ММ-ДД");
        }
        if (parts[2].isBlank()) {
            throw new IllegalArgumentException("не вказано інспектора");
        }
        try {
            category = Integer.parseInt(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("condition_category має бути числом");
        }
        return new Inspection(null, objectId, parts[1].trim(), parts[2].trim(), category, parts[4].trim());
    }
}

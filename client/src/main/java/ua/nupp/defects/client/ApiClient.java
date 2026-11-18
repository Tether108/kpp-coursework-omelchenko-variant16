package ua.nupp.defects.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.nupp.defects.client.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// усі запити до сервера йдуть тільки через цей клас
public class ApiClient {
    private static final Logger LOG = Logger.getLogger(ApiClient.class.getName());
    private static final String BASE = "http://localhost:8080/building-defects";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // ---- будівельні об'єкти ----
    public List<BuildingObject> getObjects() {
        return Arrays.asList(read(get("/objects"), BuildingObject[].class));
    }

    public void addObject(BuildingObject o) {
        post("/objects", "action=add&" + objectForm(o));
    }

    public void updateObject(BuildingObject o) {
        post("/objects", "action=update&id=" + o.getId() + "&" + objectForm(o));
    }

    public void deleteObject(long id) {
        post("/objects", "action=delete&id=" + id);
    }

    // ---- обстеження ----
    public List<Inspection> getInspections() {
        return Arrays.asList(read(get("/inspections"), Inspection[].class));
    }

    public void addInspection(Inspection i) {
        post("/inspections", "action=add&" + inspectionForm(i));
    }

    public void updateInspection(Inspection i) {
        post("/inspections", "action=update&id=" + i.getId() + "&" + inspectionForm(i));
    }

    public void deleteInspection(long id) {
        post("/inspections", "action=delete&id=" + id);
    }

    // ---- дефекти ----
    public List<Defect> getDefects() {
        return Arrays.asList(read(get("/defects"), Defect[].class));
    }

    public void addDefect(Defect d) {
        post("/defects", "action=add&" + defectForm(d));
    }

    public void updateDefect(Defect d) {
        post("/defects", "action=update&id=" + d.getId() + "&" + defectForm(d));
    }

    public void changeDefectState(long id, DefectState state) {
        post("/defects", "action=transition&id=" + id + "&state=" + state.name());
    }

    public void deleteDefect(long id) {
        post("/defects", "action=delete&id=" + id);
    }

    // ---- ремонтні завдання ----
    public List<RepairTask> getTasks() {
        return Arrays.asList(read(get("/tasks"), RepairTask[].class));
    }

    public void addTask(RepairTask t) {
        post("/tasks", "action=add&" + taskForm(t));
    }

    public void updateTask(RepairTask t) {
        post("/tasks", "action=update&id=" + t.getId() + "&" + taskForm(t));
    }

    public void deleteTask(long id) {
        post("/tasks", "action=delete&id=" + id);
    }

    // ---- параметри для POST ----
    private String objectForm(BuildingObject o) {
        return "name=" + enc(o.getName()) + "&address=" + enc(o.getAddress())
                + "&objectType=" + enc(o.getObjectType()) + "&buildYear=" + o.getBuildYear();
    }

    private String inspectionForm(Inspection i) {
        return "objectId=" + i.getObjectId() + "&inspectionDate=" + enc(i.getInspectionDate())
                + "&inspector=" + enc(i.getInspector()) + "&conditionCategory=" + i.getConditionCategory()
                + "&notes=" + enc(i.getNotes());
    }

    private String defectForm(Defect d) {
        return "inspectionId=" + d.getInspectionId() + "&description=" + enc(d.getDescription())
                + "&severity=" + d.getSeverity() + "&area=" + d.getArea() + "&safetyScore=" + d.getSafetyScore();
    }

    private String taskForm(RepairTask t) {
        return "defectId=" + t.getDefectId() + "&description=" + enc(t.getDescription())
                + "&executor=" + enc(t.getExecutor()) + "&plannedDate=" + enc(t.getPlannedDate());
    }

    // ---- HTTP ----
    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                .build();
        return send(req);
    }

    private String send(HttpRequest req) {
        HttpResponse<String> response;
        try {
            response = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            LOG.warning("Сервер недоступний: " + e);
            throw new RuntimeException("Сервер недоступний. Перевірте, чи запущено Tomcat");
        }
        if (response.statusCode() >= 400) {
            String message = readError(response.body());
            LOG.warning("Сервер повернув помилку " + response.statusCode() + ": " + message);
            throw new RuntimeException(message);
        }
        return response.body();
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            LOG.warning("Помилка JSON: " + e.getMessage());
            throw new RuntimeException("Помилка JSON: сервер повернув некоректну відповідь");
        }
    }

    // сервер повертає помилку
    private String readError(String body) {
        try {
            return mapper.readTree(body).get("error").asText();
        } catch (Exception e) {
            return "Помилка сервера";
        }
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}

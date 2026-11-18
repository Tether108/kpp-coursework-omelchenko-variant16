package ua.nupp.defects.servlet;

import ua.nupp.defects.model.Defect;
import ua.nupp.defects.model.DefectState;
import ua.nupp.defects.service.DefectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefectServlet extends BaseServlet {
    private final DefectService service = new DefectService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                service.add(read(req, null));
            } else if ("update".equals(action)) {
                service.update(read(req, id(req)));
            } else if ("transition".equals(action)) {
                service.changeState(id(req), state(req));
            } else if ("delete".equals(action)) {
                service.delete(id(req));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    // стан і F1, F2, F3, I сюди не читаємо - їх задає сервер
    private Defect read(HttpServletRequest req, Long id) {
        Defect d = new Defect();
        d.setId(id);
        d.setInspectionId(Long.parseLong(req.getParameter("inspectionId")));
        d.setDescription(req.getParameter("description"));
        d.setSeverity(Integer.parseInt(req.getParameter("severity")));
        d.setArea(Double.parseDouble(req.getParameter("area")));
        d.setSafetyScore(Integer.parseInt(req.getParameter("safetyScore")));
        return d;
    }

    private DefectState state(HttpServletRequest req) {
        String value = req.getParameter("state");
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Не вказано новий стан");
        }
        try {
            return DefectState.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Невідомий стан: " + value);
        }
    }
}

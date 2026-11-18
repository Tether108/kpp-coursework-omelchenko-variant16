package ua.nupp.defects.servlet;

import ua.nupp.defects.model.Inspection;
import ua.nupp.defects.service.InspectionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InspectionServlet extends BaseServlet {
    private final InspectionService service = new InspectionService();

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

    private Inspection read(HttpServletRequest req, Long id) {
        return new Inspection(id,
                Long.parseLong(req.getParameter("objectId")),
                req.getParameter("inspectionDate"),
                req.getParameter("inspector"),
                Integer.parseInt(req.getParameter("conditionCategory")),
                req.getParameter("notes"));
    }
}

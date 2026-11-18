package ua.nupp.defects.servlet;

import ua.nupp.defects.model.RepairTask;
import ua.nupp.defects.service.RepairTaskService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RepairTaskServlet extends BaseServlet {
    private final RepairTaskService service = new RepairTaskService();

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

    private RepairTask read(HttpServletRequest req, Long id) {
        return new RepairTask(id,
                Long.parseLong(req.getParameter("defectId")),
                req.getParameter("description"),
                req.getParameter("executor"),
                req.getParameter("plannedDate"));
    }
}

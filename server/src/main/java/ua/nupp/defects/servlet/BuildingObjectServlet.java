package ua.nupp.defects.servlet;

import ua.nupp.defects.model.BuildingObject;
import ua.nupp.defects.service.BuildingObjectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BuildingObjectServlet extends BaseServlet {
    private final BuildingObjectService service = new BuildingObjectService();

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

    private BuildingObject read(HttpServletRequest req, Long id) {
        return new BuildingObject(id,
                req.getParameter("name"),
                req.getParameter("address"),
                req.getParameter("objectType"),
                Integer.parseInt(req.getParameter("buildYear")));
    }
}

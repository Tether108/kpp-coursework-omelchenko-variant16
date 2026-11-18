package ua.nupp.defects.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// спільні методи для всіх сервлетів, щоб не повторювати їх у кожному
public abstract class BaseServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();

    protected void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    // IllegalArgumentException - помилка в даних (400), все інше - помилка сервера (500)
    protected void writeError(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            String message = e.getMessage();
            if (e instanceof NumberFormatException || message == null) {
                message = "Некоректне числове значення у запиті";
            }
            LOG.warning("Некоректний запит: " + message);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, Map.of("error", message));
        } else {
            LOG.log(Level.SEVERE, "Помилка на сервері", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, Map.of("error", "Внутрішня помилка сервера"));
        }
    }

    protected long id(HttpServletRequest req) {
        return Long.parseLong(req.getParameter("id"));
    }
}

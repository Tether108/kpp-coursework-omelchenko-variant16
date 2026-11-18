package ua.nupp.defects.service;

import ua.nupp.defects.dao.BuildingObjectDao;
import ua.nupp.defects.model.BuildingObject;
import ua.nupp.defects.util.Check;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class BuildingObjectService {
    private static final Logger LOG = Logger.getLogger(BuildingObjectService.class.getName());
    private final BuildingObjectDao dao = new BuildingObjectDao();

    public List<BuildingObject> getAll() throws SQLException {
        return dao.findAll();
    }

    public BuildingObject add(BuildingObject o) throws SQLException {
        validate(o);
        long id = dao.insert(o);
        o.setId(id);
        LOG.info("Додано будівельний об'єкт, id=" + id);
        return o;
    }

    public BuildingObject update(BuildingObject o) throws SQLException {
        validate(o);
        if (o.getId() == null || !dao.update(o)) {
            throw new IllegalArgumentException("Будівельний об'єкт не знайдено");
        }
        LOG.info("Змінено будівельний об'єкт, id=" + o.getId());
        return o;
    }

    public void delete(long id) throws SQLException {
        try {
            if (!dao.delete(id)) {
                throw new IllegalArgumentException("Будівельний об'єкт з id=" + id + " не знайдено");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Не можна видалити об'єкт, бо для нього вже є обстеження");
        }
        LOG.info("Видалено будівельний об'єкт, id=" + id);
    }

    public static void validate(BuildingObject o) {
        if (o == null) {
            throw new IllegalArgumentException("Не передано дані об'єкта");
        }
        Check.notEmpty(o.getName(), "Назва об'єкта не може бути порожньою");
        Check.notEmpty(o.getAddress(), "Адреса не може бути порожньою");
        Check.notEmpty(o.getObjectType(), "Тип об'єкта не може бути порожнім");
        int year = LocalDate.now().getYear();
        if (o.getBuildYear() < 1800 || o.getBuildYear() > year) {
            throw new IllegalArgumentException("Рік побудови має бути від 1800 до " + year);
        }
    }
}

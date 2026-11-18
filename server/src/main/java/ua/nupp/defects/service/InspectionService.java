package ua.nupp.defects.service;

import ua.nupp.defects.dao.BuildingObjectDao;
import ua.nupp.defects.dao.InspectionDao;
import ua.nupp.defects.model.Inspection;
import ua.nupp.defects.util.Check;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class InspectionService {
    private static final Logger LOG = Logger.getLogger(InspectionService.class.getName());
    private final InspectionDao dao = new InspectionDao();
    private final BuildingObjectDao objectDao = new BuildingObjectDao();

    public List<Inspection> getAll() throws SQLException {
        return dao.findAll();
    }

    public Inspection add(Inspection i) throws SQLException {
        validate(i);
        checkObject(i.getObjectId());
        long id = dao.insert(i);
        i.setId(id);
        LOG.info("Додано обстеження, id=" + id + ", об'єкт " + i.getObjectId());
        return i;
    }

    public Inspection update(Inspection i) throws SQLException {
        validate(i);
        checkObject(i.getObjectId());
        if (i.getId() == null || !dao.update(i)) {
            throw new IllegalArgumentException("Обстеження не знайдено");
        }
        LOG.info("Змінено обстеження, id=" + i.getId());
        return i;
    }

    public void delete(long id) throws SQLException {
        try {
            if (!dao.delete(id)) {
                throw new IllegalArgumentException("Обстеження з id=" + id + " не знайдено");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Не можна видалити обстеження, бо до нього прив'язані дефекти");
        }
        LOG.info("Видалено обстеження, id=" + id);
    }

    private void checkObject(long objectId) throws SQLException {
        if (objectDao.findById(objectId) == null) {
            throw new IllegalArgumentException("Будівельний об'єкт з id=" + objectId + " не існує");
        }
    }

    public static void validate(Inspection i) {
        if (i == null) {
            throw new IllegalArgumentException("Не передано дані обстеження");
        }
        if (i.getObjectId() <= 0) {
            throw new IllegalArgumentException("Не вказано будівельний об'єкт");
        }
        LocalDate date = Check.date(i.getInspectionDate(), "Дата обстеження має бути у форматі РРРР-ММ-ДД");
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата обстеження не може бути в майбутньому");
        }
        Check.notEmpty(i.getInspector(), "Не вказано інспектора");
        if (i.getConditionCategory() < 1 || i.getConditionCategory() > 4) {
            throw new IllegalArgumentException("Категорія технічного стану має бути від 1 до 4");
        }
    }
}

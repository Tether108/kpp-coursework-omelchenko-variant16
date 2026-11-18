package ua.nupp.defects.service;

import ua.nupp.defects.dao.DefectDao;
import ua.nupp.defects.dao.RepairTaskDao;
import ua.nupp.defects.model.RepairTask;
import ua.nupp.defects.util.Check;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class RepairTaskService {
    private static final Logger LOG = Logger.getLogger(RepairTaskService.class.getName());
    private final RepairTaskDao dao = new RepairTaskDao();
    private final DefectDao defectDao = new DefectDao();

    public List<RepairTask> getAll() throws SQLException {
        return dao.findAll();
    }

    public RepairTask add(RepairTask t) throws SQLException {
        validate(t);
        checkDefect(t.getDefectId());
        long id = dao.insert(t);
        t.setId(id);
        LOG.info("Додано ремонтне завдання, id=" + id + ", дефект " + t.getDefectId());
        return t;
    }

    public RepairTask update(RepairTask t) throws SQLException {
        validate(t);
        checkDefect(t.getDefectId());
        if (t.getId() == null || !dao.update(t)) {
            throw new IllegalArgumentException("Ремонтне завдання не знайдено");
        }
        LOG.info("Змінено ремонтне завдання, id=" + t.getId());
        return t;
    }

    public void delete(long id) throws SQLException {
        if (!dao.delete(id)) {
            throw new IllegalArgumentException("Ремонтне завдання з id=" + id + " не знайдено");
        }
        LOG.info("Видалено ремонтне завдання, id=" + id);
    }

    private void checkDefect(long defectId) throws SQLException {
        if (defectDao.findById(defectId) == null) {
            throw new IllegalArgumentException("Дефект з id=" + defectId + " не існує");
        }
    }

    public static void validate(RepairTask t) {
        if (t == null) {
            throw new IllegalArgumentException("Не передано дані завдання");
        }
        if (t.getDefectId() <= 0) {
            throw new IllegalArgumentException("Не вказано дефект");
        }
        Check.notEmpty(t.getDescription(), "Опис робіт не може бути порожнім");
        Check.notEmpty(t.getExecutor(), "Не вказано виконавця");
        Check.date(t.getPlannedDate(), "Планова дата має бути у форматі РРРР-ММ-ДД");
    }
}

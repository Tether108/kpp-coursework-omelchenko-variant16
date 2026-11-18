package ua.nupp.defects.service;

import ua.nupp.defects.dao.DefectDao;
import ua.nupp.defects.dao.InspectionDao;
import ua.nupp.defects.model.Defect;
import ua.nupp.defects.model.DefectState;
import ua.nupp.defects.util.Check;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

public class DefectService {
    private static final Logger LOG = Logger.getLogger(DefectService.class.getName());
    private final DefectDao dao = new DefectDao();
    private final InspectionDao inspectionDao = new InspectionDao();

    public List<Defect> getAll() throws SQLException {
        return dao.findAll();
    }

    public Defect add(Defect d) throws SQLException {
        validate(d);
        checkInspection(d.getInspectionId());
        d.setState(DefectState.REGISTERED);
        calculateIndex(d);
        long id = dao.insert(d);
        d.setId(id);
        LOG.info("Зафіксовано дефект, id=" + id + ", I=" + d.getRiskIndex());
        return d;
    }

    public Defect update(Defect d) throws SQLException {
        validate(d);
        if (d.getId() == null) {
            throw new IllegalArgumentException("Дефект не знайдено");
        }
        Defect old = dao.findById(d.getId());
        if (old == null) {
            throw new IllegalArgumentException("Дефект з id=" + d.getId() + " не знайдено");
        }
        checkInspection(d.getInspectionId());
        d.setState(old.getState());
        calculateIndex(d);
        dao.update(d);
        LOG.info("Змінено дефект, id=" + d.getId() + ", I=" + d.getRiskIndex());
        return d;
    }

    public Defect changeState(long id, DefectState newState) throws SQLException {
        Defect d = dao.findById(id);
        if (d == null) {
            throw new IllegalArgumentException("Дефект з id=" + id + " не знайдено");
        }
        if (!canChangeState(d.getState(), newState)) {
            LOG.warning("Відхилено перехід дефекту " + id + ": " + d.getState() + " -> " + newState);
            throw new IllegalArgumentException("Недопустимий перехід: " + d.getState().getTitle()
                    + " -> " + (newState == null ? "?" : newState.getTitle()));
        }
        dao.updateState(id, newState);
        LOG.info("Дефект " + id + ": " + d.getState() + " -> " + newState);
        d.setState(newState);
        return d;
    }

    public void delete(long id) throws SQLException {
        try {
            if (!dao.delete(id)) {
                throw new IllegalArgumentException("Дефект з id=" + id + " не знайдено");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Не можна видалити дефект, бо для нього є ремонтні завдання");
        }
        LOG.info("Видалено дефект, id=" + id);
    }

    // дозволено тільки по черзі: ЗАФІКСОВАНО -> ПЕРЕВІРЕНО -> РЕМОНТ_ПРИЗНАЧЕНО -> ЗАКРИТО
    public static boolean canChangeState(DefectState from, DefectState to) {
        if (from == null || to == null) {
            return false;
        }
        switch (from) {
            case REGISTERED:
                return to == DefectState.CHECKED;
            case CHECKED:
                return to == DefectState.REPAIR_ASSIGNED;
            case REPAIR_ASSIGNED:
                return to == DefectState.CLOSED;
            default:
                return false;
        }
    }

    public static void calculateIndex(Defect d) {
        double f1 = IndexCalculator.severityFactor(d.getSeverity());
        double f2 = IndexCalculator.areaFactor(d.getArea());
        double f3 = IndexCalculator.safetyFactor(d.getSafetyScore());
        d.setF1(f1);
        d.setF2(f2);
        d.setF3(f3);
        d.setRiskIndex(IndexCalculator.calculate(f1, f2, f3));
    }

    private void checkInspection(long inspectionId) throws SQLException {
        if (inspectionDao.findById(inspectionId) == null) {
            throw new IllegalArgumentException("Обстеження з id=" + inspectionId + " не існує");
        }
    }

    public static void validate(Defect d) {
        if (d == null) {
            throw new IllegalArgumentException("Не передано дані дефекту");
        }
        if (d.getInspectionId() <= 0) {
            throw new IllegalArgumentException("Не вказано обстеження");
        }
        Check.notEmpty(d.getDescription(), "Опис дефекту не може бути порожнім");
        IndexCalculator.severityFactor(d.getSeverity());
        IndexCalculator.areaFactor(d.getArea());
        IndexCalculator.safetyFactor(d.getSafetyScore());
    }
}

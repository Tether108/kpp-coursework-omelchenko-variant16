package ua.nupp.defects;

import org.junit.jupiter.api.Test;
import ua.nupp.defects.model.BuildingObject;
import ua.nupp.defects.model.Defect;
import ua.nupp.defects.model.Inspection;
import ua.nupp.defects.model.RepairTask;
import ua.nupp.defects.service.BuildingObjectService;
import ua.nupp.defects.service.DefectService;
import ua.nupp.defects.service.InspectionService;
import ua.nupp.defects.service.RepairTaskService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    @Test
    void correctObjectPasses() {
        BuildingObject o = new BuildingObject(null, "Гуртожиток №3", "вул. Шевченка, 12", "Житловий будинок", 1985);
        assertDoesNotThrow(() -> BuildingObjectService.validate(o));
    }

    @Test
    void inspectionInFutureRejected() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        Inspection i = new Inspection(null, 1, tomorrow, "Іваненко П.", 2, "");
        assertThrows(IllegalArgumentException.class, () -> InspectionService.validate(i));
    }

    @Test
    void defectWithoutDescriptionRejected() {
        Defect d = new Defect(null, 1, "", 3, 5, 50, null, 0, 0, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> DefectService.validate(d));
    }

    @Test
    void repairTaskWithoutExecutorRejected() {
        RepairTask t = new RepairTask(null, 1, "Закласти тріщину", "", "2026-12-01");
        assertThrows(IllegalArgumentException.class, () -> RepairTaskService.validate(t));
    }
}

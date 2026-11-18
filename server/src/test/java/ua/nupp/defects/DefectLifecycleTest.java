package ua.nupp.defects;

import org.junit.jupiter.api.Test;
import ua.nupp.defects.model.Defect;
import ua.nupp.defects.model.DefectState;
import ua.nupp.defects.service.DefectService;

import static org.junit.jupiter.api.Assertions.*;

public class DefectLifecycleTest {

    @Test
    void registeredToCheckedAllowed() {
        assertTrue(DefectService.canChangeState(DefectState.REGISTERED, DefectState.CHECKED));
    }

    @Test
    void registeredToClosedForbidden() {
        assertFalse(DefectService.canChangeState(DefectState.REGISTERED, DefectState.CLOSED));
    }

    @Test
    void closedCannotGoBack() {
        assertFalse(DefectService.canChangeState(DefectState.CLOSED, DefectState.REGISTERED));
    }

    @Test
    void indexIsCalculatedForDefect() {
        Defect d = new Defect(null, 1, "Тріщина в несучій стіні", 4, 10, 80, null, 0, 0, 0, 0);
        DefectService.calculateIndex(d);
        assertEquals(75.0, d.getF1());
        assertEquals(40.0, d.getF2());
        assertEquals(80.0, d.getF3());
        assertEquals(64.0, d.getRiskIndex());
    }
}

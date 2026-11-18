package ua.nupp.defects.client.model;

public class Inspection {
    private Long id;
    private long objectId;
    private String inspectionDate;
    private String inspector;
    private int conditionCategory;
    private String notes;

    public Inspection() {
    }

    public Inspection(Long id, long objectId, String inspectionDate, String inspector, int conditionCategory, String notes) {
        this.id = id;
        this.objectId = objectId;
        this.inspectionDate = inspectionDate;
        this.inspector = inspector;
        this.conditionCategory = conditionCategory;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public int getConditionCategory() {
        return conditionCategory;
    }

    public void setConditionCategory(int conditionCategory) {
        this.conditionCategory = conditionCategory;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

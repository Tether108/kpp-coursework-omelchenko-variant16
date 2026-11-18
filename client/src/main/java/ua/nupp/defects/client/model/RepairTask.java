package ua.nupp.defects.client.model;

public class RepairTask {
    private Long id;
    private long defectId;
    private String description;
    private String executor;
    private String plannedDate;

    public RepairTask() {
    }

    public RepairTask(Long id, long defectId, String description, String executor, String plannedDate) {
        this.id = id;
        this.defectId = defectId;
        this.description = description;
        this.executor = executor;
        this.plannedDate = plannedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDefectId() {
        return defectId;
    }

    public void setDefectId(long defectId) {
        this.defectId = defectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(String plannedDate) {
        this.plannedDate = plannedDate;
    }
}

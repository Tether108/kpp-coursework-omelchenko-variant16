package ua.nupp.defects.model;

public class Defect {
    private Long id;
    private long inspectionId;
    private String description;
    private int severity;
    private double area;
    private int safetyScore;
    private DefectState state;
    private double f1;
    private double f2;
    private double f3;
    private double riskIndex;

    public Defect() {
    }

    public Defect(Long id, long inspectionId, String description, int severity, double area, int safetyScore,
                  DefectState state, double f1, double f2, double f3, double riskIndex) {
        this.id = id;
        this.inspectionId = inspectionId;
        this.description = description;
        this.severity = severity;
        this.area = area;
        this.safetyScore = safetyScore;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.riskIndex = riskIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(long inspectionId) {
        this.inspectionId = inspectionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getSafetyScore() {
        return safetyScore;
    }

    public void setSafetyScore(int safetyScore) {
        this.safetyScore = safetyScore;
    }

    public DefectState getState() {
        return state;
    }

    public void setState(DefectState state) {
        this.state = state;
    }

    public double getF1() {
        return f1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public double getF2() {
        return f2;
    }

    public void setF2(double f2) {
        this.f2 = f2;
    }

    public double getF3() {
        return f3;
    }

    public void setF3(double f3) {
        this.f3 = f3;
    }

    public double getRiskIndex() {
        return riskIndex;
    }

    public void setRiskIndex(double riskIndex) {
        this.riskIndex = riskIndex;
    }
}

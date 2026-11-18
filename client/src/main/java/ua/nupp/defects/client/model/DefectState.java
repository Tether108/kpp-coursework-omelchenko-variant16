package ua.nupp.defects.client.model;

public enum DefectState {
    REGISTERED("ЗАФІКСОВАНО"),
    CHECKED("ПЕРЕВІРЕНО"),
    REPAIR_ASSIGNED("РЕМОНТ_ПРИЗНАЧЕНО"),
    CLOSED("ЗАКРИТО");

    private final String title;

    DefectState(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}

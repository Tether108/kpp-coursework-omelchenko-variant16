package ua.nupp.defects.client.model;

public class BuildingObject {
    private Long id;
    private String name;
    private String address;
    private String objectType;
    private int buildYear;

    public BuildingObject() {
    }

    public BuildingObject(Long id, String name, String address, String objectType, int buildYear) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.objectType = objectType;
        this.buildYear = buildYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public int getBuildYear() {
        return buildYear;
    }

    public void setBuildYear(int buildYear) {
        this.buildYear = buildYear;
    }
}

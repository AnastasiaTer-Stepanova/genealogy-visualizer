package genealogy.visualizer.dto;

import genealogy.visualizer.entity.enums.LocalityType;

public class LocalityFilterDTO {

    private String name;

    private LocalityType type;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalityType getType() {
        return type;
    }

    public void setType(LocalityType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

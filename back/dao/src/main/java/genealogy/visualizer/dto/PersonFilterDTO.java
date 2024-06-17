package genealogy.visualizer.dto;

import genealogy.visualizer.entity.enums.Sex;

import java.util.ArrayList;
import java.util.List;

public class PersonFilterDTO {

    private FullNameFilterDTO fullName;

    private Integer birthYear;

    private Integer deathYear;

    private Sex sex;

    private List<String> graphs = new ArrayList<>();

    public PersonFilterDTO() {
    }

    public PersonFilterDTO(List<String> graphs) {
        this.graphs = graphs;
    }

    public FullNameFilterDTO getFullName() {
        return fullName;
    }

    public void setFullName(FullNameFilterDTO fullName) {
        this.fullName = fullName;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public List<String> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<String> graphs) {
        this.graphs = graphs;
    }
}

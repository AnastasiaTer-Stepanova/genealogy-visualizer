package genealogy.visualizer.entity;

import genealogy.visualizer.converter.LocalityTypeConverter;
import genealogy.visualizer.entity.enums.LocalityType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Embeddable
@Table(indexes = {@Index(name = "IDX_LOCALITY_NAME", columnList = "NAME")})
public class Locality implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LOCALITY")
    @SequenceGenerator(name = "SEQ_LOCALITY", sequenceName = "SEQ_LOCALITY", allocationSize = 1)
    @Comment("Идентификатор записи")
    private Long id;

    @Column(length = 50, nullable = false)
    @Comment("Наименование города")
    private String name;

    @Column(length = 20)
    @Comment("Тип: город, село, деревня и т.д.")
    @Convert(converter = LocalityTypeConverter.class)
    private LocalityType type;

    @Comment("Полный адрес: страна, область, округ и т.д.")
    private String address;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "ANOTHER_LOCALITY_NAME",
            joinColumns = @JoinColumn(name = "LOCALITY_ID",
                    foreignKey = @ForeignKey(name = "FK_ANOTHER_LOCALITY_NAME")),
            uniqueConstraints = @UniqueConstraint(name = "UK_ANOTHER_LOCALITY_NAME",
                    columnNames = {"LOCALITY_ID", "ANOTHER_NAME"}))
    @Column(name = "ANOTHER_NAME", length = 50)
    private List<String> anotherNames = new ArrayList<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<Christening> christenings = new ArrayList<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<Death> deaths = new ArrayList<>();

    public Locality() {
    }

    public Locality(String name, LocalityType type) {
        this.name = name;
        this.type = type;
    }

    public Locality(String name, LocalityType type, String address, List<String> anotherNames) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.anotherNames = anotherNames;
    }

    public Locality(Long id, String name, LocalityType type, String address, List<String> anotherNames, List<Christening> christenings, List<Death> deaths) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.anotherNames = anotherNames;
        this.christenings = christenings;
        this.deaths = deaths;
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

    public List<String> getAnotherNames() {
        return anotherNames;
    }

    public void setAnotherNames(List<String> anotherNames) {
        this.anotherNames = anotherNames;
    }

    public List<Christening> getChristenings() {
        return christenings;
    }

    public void setChristenings(List<Christening> christenings) {
        this.christenings = christenings;
    }

    public List<Death> getDeaths() {
        return deaths;
    }

    public void setDeaths(List<Death> deaths) {
        this.deaths = deaths;
    }
}

package genealogy.visualizer.entity;

import genealogy.visualizer.converter.LocalityTypeConverter;
import genealogy.visualizer.entity.enums.LocalityType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
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
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
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
    @CollectionTable(name = "ANOTHER_LOCALITY_NAMES",
            joinColumns = @JoinColumn(name = "ANOTHER_NAME_ID",
                    foreignKey = @ForeignKey(name = "FK_ANOTHER_LOCALITY_NAMES")))
    private List<String> anotherNames = new ArrayList<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<Christening> christenings = new ArrayList<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<GodParent> godParents = new ArrayList<>();

    public Locality() {
    }

    public Locality(String name, LocalityType type) {
        this.name = name;
        this.type = type;
    }

    public Locality(Long id, String name, LocalityType type, String address, List<String> anotherNames, List<Christening> christenings, List<GodParent> godParents) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.anotherNames = anotherNames;
        this.christenings = christenings;
        this.godParents = godParents;
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
        if (anotherNames == null) {
            return new ArrayList<>();
        }
        return anotherNames;
    }

    public void setAnotherNames(List<String> anotherNames) {
        this.anotherNames = anotherNames;
    }

    public List<Christening> getChristenings() {
        if (christenings == null) {
            return new ArrayList<>();
        }
        return christenings;
    }

    public void setChristenings(List<Christening> christenings) {
        this.christenings = christenings;
    }

    public List<GodParent> getGodParents() {
        if (godParents == null) {
            return new ArrayList<>();
        }
        return godParents;
    }

    public void setGodParents(List<GodParent> godParents) {
        this.godParents = godParents;
    }
}
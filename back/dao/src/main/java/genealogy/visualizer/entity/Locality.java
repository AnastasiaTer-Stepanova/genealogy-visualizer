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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Embeddable
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Locality.withAnotherNames", attributeNodes = {@NamedAttributeNode(value = "anotherNames"),}),
        @NamedEntityGraph(name = "Locality.withDeaths", attributeNodes = {@NamedAttributeNode(value = "deaths"),}),
        @NamedEntityGraph(name = "Locality.withChristenings", attributeNodes = {@NamedAttributeNode(value = "christenings")}),
        @NamedEntityGraph(name = "Locality.withPersonsWithBirthLocality",
                attributeNodes = {@NamedAttributeNode(value = "personsWithBirthLocality", subgraph = "personGraph")},
                subgraphs = {@NamedSubgraph(name = "personGraph", attributeNodes = {@NamedAttributeNode("christening"), @NamedAttributeNode("death"),})}),
        @NamedEntityGraph(name = "Locality.withPersonsWithDeathLocality",
                attributeNodes = {@NamedAttributeNode(value = "personsWithDeathLocality", subgraph = "personGraph")},
                subgraphs = {@NamedSubgraph(name = "personGraph", attributeNodes = {@NamedAttributeNode("christening"), @NamedAttributeNode("death"),})}),
        @NamedEntityGraph(name = "Locality.withMarriagesWithWifeLocality",
                attributeNodes = {@NamedAttributeNode(value = "marriagesWithWifeLocality"),}),
        @NamedEntityGraph(name = "Locality.withMarriagesWithHusbandLocality",
                attributeNodes = {@NamedAttributeNode(value = "marriagesWithHusbandLocality")}),
        @NamedEntityGraph(name = "Locality.withGodParents",
                attributeNodes = {@NamedAttributeNode(value = "godParents", subgraph = "godParentGraph")},
                subgraphs = {
                        @NamedSubgraph(name = "godParentGraph", type = Witness.class,
                                attributeNodes = {@NamedAttributeNode(value = "locality", subgraph = "localityGraph")}),
                        @NamedSubgraph(name = "localityGraph", type = Locality.class,
                                attributeNodes = {@NamedAttributeNode("anotherNames")})
                }),
        @NamedEntityGraph(name = "Locality.withWitnesses",
                attributeNodes = {@NamedAttributeNode(value = "witnesses", subgraph = "witnessGraph")},
                subgraphs = {
                        @NamedSubgraph(name = "witnessGraph", type = Witness.class,
                                attributeNodes = {@NamedAttributeNode(value = "locality", subgraph = "localityGraph")}),
                        @NamedSubgraph(name = "localityGraph", type = Locality.class,
                                attributeNodes = {@NamedAttributeNode("anotherNames")})
                }),
})
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

    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "ANOTHER_LOCALITY_NAME",
            joinColumns = @JoinColumn(name = "LOCALITY_ID",
                    foreignKey = @ForeignKey(name = "FK_ANOTHER_LOCALITY_NAME")),
            uniqueConstraints = @UniqueConstraint(name = "UK_ANOTHER_LOCALITY_NAME",
                    columnNames = {"LOCALITY_ID", "ANOTHER_NAME"}))
    @Column(name = "ANOTHER_NAME", length = 50)
    private Set<String> anotherNames = new HashSet<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<Christening> christenings = new ArrayList<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<Death> deaths = new ArrayList<>();

    @OneToMany(mappedBy = "birthLocality", fetch = FetchType.LAZY)
    private List<Person> personsWithBirthLocality = new ArrayList<>();

    @OneToMany(mappedBy = "deathLocality", fetch = FetchType.LAZY)
    private List<Person> personsWithDeathLocality = new ArrayList<>();

    @OneToMany(mappedBy = "husbandLocality", fetch = FetchType.LAZY)
    private List<Marriage> marriagesWithHusbandLocality = new ArrayList<>();

    @OneToMany(mappedBy = "wifeLocality", fetch = FetchType.LAZY)
    private List<Marriage> marriagesWithWifeLocality = new ArrayList<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<Witness> witnesses = new ArrayList<>();

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
    private List<GodParent> godParents = new ArrayList<>();

    public Locality() {
    }

    public Locality(String name, LocalityType type) {
        this.name = name;
        this.type = type;
    }

    public Locality(String name, LocalityType type, String address, Set<String> anotherNames) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.anotherNames = anotherNames;
    }

    public Locality(Long id, String name, LocalityType type, String address, Set<String> anotherNames, List<Christening> christenings, List<Death> deaths, List<Person> personsWithBirthLocality, List<Person> personsWithDeathLocality, List<Marriage> marriagesWithHusbandLocality, List<Marriage> marriagesWithWifeLocality, List<Witness> witnesses, List<GodParent> godParents) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.anotherNames = anotherNames;
        this.christenings = christenings;
        this.deaths = deaths;
        this.personsWithBirthLocality = personsWithBirthLocality;
        this.personsWithDeathLocality = personsWithDeathLocality;
        this.marriagesWithHusbandLocality = marriagesWithHusbandLocality;
        this.marriagesWithWifeLocality = marriagesWithWifeLocality;
        this.witnesses = witnesses;
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

    public Set<String> getAnotherNames() {
        return anotherNames;
    }

    public void setAnotherNames(Set<String> anotherNames) {
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

    public List<Person> getPersonsWithBirthLocality() {
        return personsWithBirthLocality;
    }

    public void setPersonsWithBirthLocality(List<Person> personsWithBirthLocality) {
        this.personsWithBirthLocality = personsWithBirthLocality;
    }

    public List<Person> getPersonsWithDeathLocality() {
        return personsWithDeathLocality;
    }

    public void setPersonsWithDeathLocality(List<Person> personsWithDeathLocality) {
        this.personsWithDeathLocality = personsWithDeathLocality;
    }

    public List<Marriage> getMarriagesWithHusbandLocality() {
        return marriagesWithHusbandLocality;
    }

    public void setMarriagesWithHusbandLocality(List<Marriage> marriagesWithHusbandLocality) {
        this.marriagesWithHusbandLocality = marriagesWithHusbandLocality;
    }

    public List<Marriage> getMarriagesWithWifeLocality() {
        return marriagesWithWifeLocality;
    }

    public void setMarriagesWithWifeLocality(List<Marriage> marriagesWithWifeLocality) {
        this.marriagesWithWifeLocality = marriagesWithWifeLocality;
    }

    public List<Witness> getWitnesses() {
        return witnesses;
    }

    public void setWitnesses(List<Witness> witnesses) {
        this.witnesses = witnesses;
    }

    public List<GodParent> getGodParents() {
        return godParents;
    }

    public void setGodParents(List<GodParent> godParents) {
        this.godParents = godParents;
    }

    public Locality clone() {
        return new Locality(id, name, type, address, anotherNames, christenings, deaths, personsWithBirthLocality,
                personsWithDeathLocality, marriagesWithHusbandLocality, marriagesWithWifeLocality, witnesses, godParents);
    }
}

package genealogy.visualizer.entity;

import genealogy.visualizer.converter.ArchiveDocumentTypeConverter;
import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "ArchiveDocument.withArchiveAndNextRevisionAndPreviousRevisions",
                attributeNodes = {
                        @NamedAttributeNode(value = "previousRevisions"),
                        @NamedAttributeNode(value = "archive"),
                        @NamedAttributeNode(value = "nextRevision")
                }),
        @NamedEntityGraph(name = "ArchiveDocument.withRevisions",
                attributeNodes = {@NamedAttributeNode(value = "familyRevisions", subgraph = "revisionGraph")},
                subgraphs = {@NamedSubgraph(name = "revisionGraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "partner", subgraph = "anotherNamesGraph"),
                                @NamedAttributeNode("anotherNames"),
                        }),
                        @NamedSubgraph(name = "anotherNamesGraph", type = String.class, attributeNodes = {
                                @NamedAttributeNode("anotherNames")})
                }),
        @NamedEntityGraph(name = "ArchiveDocument.withDeaths", attributeNodes = {@NamedAttributeNode(value = "deaths")}),
        @NamedEntityGraph(name = "ArchiveDocument.withChristenings", attributeNodes = {@NamedAttributeNode(value = "christenings")}),
        @NamedEntityGraph(name = "ArchiveDocument.withMarriages", attributeNodes = {@NamedAttributeNode(value = "marriages")}),
})
@Table(indexes = @Index(name = "IDX_INSTANCE_IN_ARCHIVE", columnList = "ARCHIVE_ID, FUND, CATALOG, INSTANCE, BUNCH, YEAR, TYPE"))
public class ArchiveDocument implements Serializable {

    @Id
    @Comment("Идентификатор записи")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ARCHIVE_DOCUMENT")
    @SequenceGenerator(name = "SEQ_ARCHIVE_DOCUMENT", sequenceName = "SEQ_ARCHIVE_DOCUMENT", allocationSize = 1)
    private Long id;

    @Column(length = 20, nullable = false)
    @Comment("Тип документа: РС - ревизская сказка, МК - метрическая книга, ИВ - исповедная ведомость и т.д.")
    @Convert(converter = ArchiveDocumentTypeConverter.class)
    private ArchiveDocumentType type;

    @Comment("Наименование документа")
    private String name;

    @Comment("Сокращенное наименование")
    @Column(length = 10)
    private String abbreviation;

    @Column(nullable = false)
    @Comment("Год написания документа")
    private Short year;

    @Column(length = 10, nullable = false)
    @Comment("Номер фонда")
    private String fund;

    @Column(length = 10, nullable = false)
    @Comment("Номер описи")
    private String catalog;

    @Column(length = 10, nullable = false)
    @Comment("Номер дела")
    private String instance;

    @Column(length = 50, nullable = false)
    @Comment("Связка")
    private String bunch;

    @OneToMany(mappedBy = "archiveDocument", fetch = FetchType.LAZY)
    private List<FamilyRevision> familyRevisions = new ArrayList<>();

    @OneToMany(mappedBy = "archiveDocument", fetch = FetchType.LAZY)
    private List<Christening> christenings = new ArrayList<>();

    @OneToMany(mappedBy = "archiveDocument", fetch = FetchType.LAZY)
    private List<Marriage> marriages = new ArrayList<>();

    @OneToMany(mappedBy = "archiveDocument", fetch = FetchType.LAZY)
    private List<Death> deaths = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ARCHIVE"))
    private Archive archive;

    @OneToMany(mappedBy = "nextRevision", fetch = FetchType.LAZY)
    private List<ArchiveDocument> previousRevisions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEXT_REVISION_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_NEXT_REVISION_ID"))
    private ArchiveDocument nextRevision;

    public ArchiveDocument() {
    }

    public ArchiveDocument(ArchiveDocumentType type, String name, String abbreviation, Short year, String fund, String catalog, String instance, String bunch, Archive archive) {
        this.type = type;
        this.name = name;
        this.abbreviation = abbreviation;
        this.year = year;
        this.fund = fund;
        this.catalog = catalog;
        this.instance = instance;
        this.bunch = bunch;
        this.archive = archive;
    }

    public ArchiveDocument(ArchiveDocumentType type, Short year, String fund, String catalog, String instance, String bunch, Archive archive) {
        this.type = type;
        this.year = year;
        this.fund = fund;
        this.catalog = catalog;
        this.instance = instance;
        this.bunch = bunch;
        this.archive = archive;
    }

    public ArchiveDocument(Long id, ArchiveDocumentType type, String name, String abbreviation, Short year, String fund, String catalog, String instance, String bunch, List<FamilyRevision> familyRevisions, List<Christening> christenings, List<Marriage> marriages, List<Death> deaths, Archive archive, List<ArchiveDocument> previousRevisions, ArchiveDocument nextRevision) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.abbreviation = abbreviation;
        this.year = year;
        this.fund = fund;
        this.catalog = catalog;
        this.instance = instance;
        this.bunch = bunch;
        this.familyRevisions = familyRevisions;
        this.christenings = christenings;
        this.marriages = marriages;
        this.deaths = deaths;
        this.archive = archive;
        this.previousRevisions = previousRevisions;
        this.nextRevision = nextRevision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArchiveDocumentType getType() {
        return type;
    }

    public void setType(ArchiveDocumentType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Short getYear() {
        return year;
    }

    public void setYear(Short year) {
        this.year = year;
    }

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getBunch() {
        return bunch;
    }

    public void setBunch(String bunch) {
        this.bunch = bunch;
    }

    public List<FamilyRevision> getFamilyRevisions() {
        return familyRevisions;
    }

    public void setFamilyRevisions(List<FamilyRevision> familyRevisions) {
        this.familyRevisions = familyRevisions;
    }

    public List<Christening> getChristenings() {
        return christenings;
    }

    public void setChristenings(List<Christening> christenings) {
        this.christenings = christenings;
    }

    public List<Marriage> getMarriages() {
        return marriages;
    }

    public void setMarriages(List<Marriage> marriages) {
        this.marriages = marriages;
    }

    public List<Death> getDeaths() {
        return deaths;
    }

    public void setDeaths(List<Death> deaths) {
        this.deaths = deaths;
    }

    public Archive getArchive() {
        return archive;
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
    }

    public List<ArchiveDocument> getPreviousRevisions() {
        return previousRevisions;
    }

    public void setPreviousRevisions(List<ArchiveDocument> previousRevisions) {
        this.previousRevisions = previousRevisions;
    }

    public ArchiveDocument getNextRevision() {
        return nextRevision;
    }

    public void setNextRevision(ArchiveDocument nextRevision) {
        this.nextRevision = nextRevision;
    }

    public ArchiveDocument clone() {
        return new ArchiveDocument(id, type, name, abbreviation, year, fund, catalog, instance, bunch, familyRevisions,
                christenings, marriages, deaths, archive, previousRevisions, nextRevision);
    }
}

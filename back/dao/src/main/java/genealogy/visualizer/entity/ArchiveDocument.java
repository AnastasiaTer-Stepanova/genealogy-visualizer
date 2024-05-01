package genealogy.visualizer.entity;

import genealogy.visualizer.converter.ArchiveDocumentTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "CONSTRAINT_INSTANCE_IN_ARCHIVE", columnNames = {"ARCHIVE_ID", "FUND", "CATALOG", "INSTANCE", "BUNCH", "YEAR"}))
public class ArchiveDocument implements Serializable {

    @Id
    @Comment("Идентификатор записи")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARCHIVE_DOCUMENT_SEQ")
    @SequenceGenerator(name = "ARCHIVE_DOCUMENT_SEQ", sequenceName = "ARCHIVE_DOCUMENT_SEQ", allocationSize = 1)
    private Long id;

    @Column(length = 10, nullable = false)
    @Comment("Тип документа: РС - ревизская сказка, МК -метрическая книга, ИВ - исповедная ведомость")
    @Convert(converter = ArchiveDocumentTypeConverter.class)
    private ArchiveDocumentType type;

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

    @Column(nullable = false)
    @Comment("Связка")
    private String bunch;

    @OneToMany(mappedBy = "archiveDocument", fetch = FetchType.LAZY)
    private List<FamilyRevision> familyRevisions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARCHIVE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_ARCHIVE"))
    private Archive archive;

    protected ArchiveDocument() {
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

    public ArchiveDocument(Long id, ArchiveDocumentType type, Short year, String fund, String catalog, String instance, String bunch, List<FamilyRevision> familyRevisions, Archive archive) {
        this.id = id;
        this.type = type;
        this.year = year;
        this.fund = fund;
        this.catalog = catalog;
        this.instance = instance;
        this.bunch = bunch;
        this.familyRevisions = familyRevisions;
        this.archive = archive;
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

    public Archive getArchive() {
        return archive;
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
    }
}

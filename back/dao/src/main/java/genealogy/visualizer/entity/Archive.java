package genealogy.visualizer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedEntityGraph(name = "Archive.full", attributeNodes = {@NamedAttributeNode("archiveDocuments")})
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_ARCHIVE_NAME", columnNames = {"NAME"}))
public class Archive implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ARCHIVE")
    @SequenceGenerator(name = "SEQ_ARCHIVE", sequenceName = "SEQ_ARCHIVE", allocationSize = 1)
    @Comment("Идентификатор сущности")
    private Long id;

    @Comment("Наименование архива")
    @Column(nullable = false)
    private String name;

    @Comment("Сокращенное наименование")
    private String abbreviation;

    @Comment("Комментарий")
    private String comment;

    @Comment("Адресс")
    private String address;

    @OneToMany(mappedBy = "archive", fetch = FetchType.LAZY)
    private List<ArchiveDocument> archiveDocuments = new ArrayList<>();

    public Archive() {
    }

    public Archive(String name) {
        this.name = name;
    }

    public Archive(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public Archive(Long id, String name, String abbreviation, String comment, String address, List<ArchiveDocument> archiveDocuments) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.comment = comment;
        this.address = address;
        this.archiveDocuments = archiveDocuments;
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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ArchiveDocument> getArchiveDocuments() {
        return archiveDocuments;
    }

    public void setArchiveDocuments(List<ArchiveDocument> archiveDocuments) {
        this.archiveDocuments = archiveDocuments;
    }

    public Archive clone() {
        return new Archive(id, name, abbreviation, comment, address, archiveDocuments);
    }
}

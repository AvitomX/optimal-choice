package ru.papapers.optimalchoice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@NamedEntityGraph(
        name = "purpose-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "criterionRelations", subgraph = "criterion-relations-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "criterion-relations-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("criterion"),
                                @NamedAttributeNode("comparingCriterion")
                        }
                )
        }
)
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@Entity
@Table(name ="PURPOSE")
@EntityListeners(AuditingEntityListener.class)
public class Purpose {

    public Purpose() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    private UUID id;

    private String name;

    @OneToMany(mappedBy="purpose", cascade = CascadeType.ALL)
    private Set<CriterionRelation> criterionRelations = new HashSet<>();

    @Version
    private Integer version;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDateTime;

}

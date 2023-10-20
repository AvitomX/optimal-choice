package ru.papapers.optimalchoice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


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
    private UUID id;

    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy="purpose", cascade = CascadeType.ALL)
    private Set<CriterionRelation> criterionRelations;

    @Version
    private Integer version;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDateTime;

}

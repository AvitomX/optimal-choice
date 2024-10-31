package ru.papapers.optimalchoice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.papapers.optimalchoice.api.domain.Estimation;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = {"id", "criterion", "comparingCriterion", "estimation"})
@Entity
@Table(name ="CRITERION_RELATIONS")
@EntityListeners(AuditingEntityListener.class)
public class CriterionRelation {

    public CriterionRelation() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="purpose_id", nullable=false)
    private Purpose purpose;

    @ManyToOne
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, org.hibernate.annotations.CascadeType.MERGE})
    @JoinColumn(name = "criterion_id")
    private Criterion criterion;

    @ManyToOne
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, org.hibernate.annotations.CascadeType.MERGE})
    @JoinColumn(name = "comparing_criterion_id")
    private Criterion comparingCriterion;

    @Enumerated(EnumType.STRING)
    private Estimation estimation;

    @Version
    private Integer version;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDateTime;

}

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

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = {"id", "criterion", "subject", "comparingSubject", "estimation"})
@Entity
@Table(name ="SUBJECT_RELATIONS")
@EntityListeners(AuditingEntityListener.class)
public class SubjectRelation {

    public SubjectRelation() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="purpose_id", nullable=false)
    private Purpose purpose;

    @ManyToOne
    @JoinColumn(name = "criterion_id")
    private Criterion criterion;

    @ManyToOne
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, org.hibernate.annotations.CascadeType.MERGE})
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, org.hibernate.annotations.CascadeType.MERGE})
    @JoinColumn(name = "comparing_subject_id")
    private Subject comparingSubject;

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

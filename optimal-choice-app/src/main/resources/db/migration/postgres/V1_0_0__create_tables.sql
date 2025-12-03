CREATE SCHEMA IF NOT EXISTS optch

    CREATE TABLE IF NOT EXISTS criterion (
      id UUID NOT NULL,
       name VARCHAR(255) NOT NULL,
       version INTEGER,
       created_date_time TIMESTAMP WITHOUT TIME ZONE,
       updated_date_time TIMESTAMP WITHOUT TIME ZONE,
       CONSTRAINT pk_criterion PRIMARY KEY (id)
    )

    CREATE TABLE IF NOT EXISTS criterion_relations (
      id UUID NOT NULL,
       purpose_id UUID NOT NULL,
       criterion_id UUID,
       comparing_criterion_id UUID,
       estimation VARCHAR(255),
       version INTEGER,
       created_date_time TIMESTAMP WITHOUT TIME ZONE,
       updated_date_time TIMESTAMP WITHOUT TIME ZONE,
       CONSTRAINT pk_criterion_relations PRIMARY KEY (id)
    )

    CREATE TABLE IF NOT EXISTS purpose (
      id UUID NOT NULL,
       name VARCHAR(255),
       version INTEGER,
       created_date_time TIMESTAMP WITHOUT TIME ZONE,
       updated_date_time TIMESTAMP WITHOUT TIME ZONE,
       CONSTRAINT pk_purpose PRIMARY KEY (id)
    )

    CREATE TABLE IF NOT EXISTS purpose_criteria (
      purpose_id UUID NOT NULL,
       criteria_id UUID NOT NULL,
       CONSTRAINT pk_purpose_criteria PRIMARY KEY (purpose_id, criteria_id)
    )

    CREATE TABLE IF NOT EXISTS purpose_subjects (
      purpose_id UUID NOT NULL,
       subjects_id UUID NOT NULL,
       CONSTRAINT pk_purpose_subjects PRIMARY KEY (purpose_id, subjects_id)
    )

    CREATE TABLE subjects (
      id UUID NOT NULL,
       name VARCHAR(255) NOT NULL,
       version INTEGER,
       created_date_time TIMESTAMP WITHOUT TIME ZONE,
       updated_date_time TIMESTAMP WITHOUT TIME ZONE,
       CONSTRAINT pk_subjects PRIMARY KEY (id)
    )

    CREATE TABLE subject_relations (
      id UUID NOT NULL,
       purpose_id UUID NOT NULL,
       criterion_id UUID,
       subject_id UUID,
       comparing_subject_id UUID,
       estimation VARCHAR(255),
       version INTEGER,
       created_date_time TIMESTAMP WITHOUT TIME ZONE,
       updated_date_time TIMESTAMP WITHOUT TIME ZONE,
       CONSTRAINT pk_subject_relations PRIMARY KEY (id)
    )

    ALTER TABLE criterion ADD CONSTRAINT uc_criterion_name UNIQUE (name)

    ALTER TABLE subjects ADD CONSTRAINT uc_subjects_name UNIQUE (name)

    ALTER TABLE criterion_relations ADD CONSTRAINT FK_CRITERION_RELATIONS_ON_COMPARING_CRITERION FOREIGN KEY (comparing_criterion_id) REFERENCES criterion (id)
    ALTER TABLE criterion_relations ADD CONSTRAINT FK_CRITERION_RELATIONS_ON_CRITERION FOREIGN KEY (criterion_id) REFERENCES criterion (id)
    ALTER TABLE criterion_relations ADD CONSTRAINT FK_CRITERION_RELATIONS_ON_PURPOSE FOREIGN KEY (purpose_id) REFERENCES purpose (id)

    ALTER TABLE purpose_criteria ADD CONSTRAINT fk_purcri_on_criterion FOREIGN KEY (criteria_id) REFERENCES criterion (id)
    ALTER TABLE purpose_criteria ADD CONSTRAINT fk_purcri_on_purpose FOREIGN KEY (purpose_id) REFERENCES purpose (id)

    ALTER TABLE purpose_subjects ADD CONSTRAINT fk_pursub_on_purpose FOREIGN KEY (purpose_id) REFERENCES purpose (id)
    ALTER TABLE purpose_subjects ADD CONSTRAINT fk_pursub_on_subject FOREIGN KEY (subjects_id) REFERENCES subjects (id)

    ALTER TABLE subject_relations ADD CONSTRAINT FK_SUBJECT_RELATIONS_ON_COMPARING_SUBJECT FOREIGN KEY (comparing_subject_id) REFERENCES subjects (id)
    ALTER TABLE subject_relations ADD CONSTRAINT FK_SUBJECT_RELATIONS_ON_CRITERION FOREIGN KEY (criterion_id) REFERENCES criterion (id)
    ALTER TABLE subject_relations ADD CONSTRAINT FK_SUBJECT_RELATIONS_ON_PURPOSE FOREIGN KEY (purpose_id) REFERENCES purpose (id)
    ALTER TABLE subject_relations ADD CONSTRAINT FK_SUBJECT_RELATIONS_ON_SUBJECT FOREIGN KEY (subject_id) REFERENCES subjects (id);

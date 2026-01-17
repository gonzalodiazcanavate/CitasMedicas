package com.gdc.medicalapp.domain.entities.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DiagnosisDiseaseId implements Serializable {

    private Long diagnosisId;
    private Long diseaseId;
}


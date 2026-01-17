package com.gdc.medicalapp.domain.entities;

import com.gdc.medicalapp.domain.entities.id.DiagnosisDiseaseId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diagnosis_diseases")
@Getter @Setter
@NoArgsConstructor
public class DiagnosisDisease {

    @EmbeddedId
    private DiagnosisDiseaseId id;

    @ManyToOne
    @MapsId("diagnosisId")
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @ManyToOne
    @MapsId("diseaseId")
    @JoinColumn(name = "disease_id")
    private Disease disease;

    private String notes;
}

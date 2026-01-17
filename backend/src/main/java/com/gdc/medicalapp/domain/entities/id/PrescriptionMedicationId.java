package com.gdc.medicalapp.domain.entities.id;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionMedicationId implements Serializable {
    private Long prescriptionId;
    private Long medicationId;
}


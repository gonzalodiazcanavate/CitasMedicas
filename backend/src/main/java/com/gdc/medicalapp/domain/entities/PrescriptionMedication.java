package com.gdc.medicalapp.domain.entities;

import com.gdc.medicalapp.domain.entities.id.PrescriptionMedicationId;
import com.gdc.medicalapp.domain.enums.MedicationFrequency;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prescription_medications")
@Getter @Setter
@NoArgsConstructor
public class PrescriptionMedication {

    @EmbeddedId
    private PrescriptionMedicationId id;

    @ManyToOne
    @MapsId("prescriptionId")
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @ManyToOne
    @MapsId("medicationId")
    @JoinColumn(name = "medication_id")
    private Medication medication;

    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicationFrequency frequency;

    private Integer durationDays;
    private String instructions;
}


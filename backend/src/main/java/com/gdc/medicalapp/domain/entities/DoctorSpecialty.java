package com.gdc.medicalapp.domain.entities;

import com.gdc.medicalapp.domain.entities.id.DoctorSpecialtyId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.gdc.medicalapp.domain.enums.ProficiencyLevel;

@Entity
@Table(name = "doctor_specialties")
@Getter @Setter
@NoArgsConstructor
public class DoctorSpecialty {

    @EmbeddedId
    private DoctorSpecialtyId id;

    @ManyToOne
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @MapsId("specialtyId")
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProficiencyLevel proficiencyLevel;
}

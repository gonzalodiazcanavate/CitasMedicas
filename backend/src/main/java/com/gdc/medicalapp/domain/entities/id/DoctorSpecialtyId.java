package com.gdc.medicalapp.domain.entities.id;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSpecialtyId implements Serializable {

    private Long doctorId;
    private Long specialtyId;
}

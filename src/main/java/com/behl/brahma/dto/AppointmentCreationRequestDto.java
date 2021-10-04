package com.behl.brahma.dto;

import java.util.Set;
import java.util.UUID;

import com.behl.brahma.constant.Prescription;
import com.behl.brahma.constant.Symptom;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class AppointmentCreationRequestDto {

    private final UUID patientId;
    private final Set<Symptom> symptoms;
    private final Set<Prescription> prescriptions;

}

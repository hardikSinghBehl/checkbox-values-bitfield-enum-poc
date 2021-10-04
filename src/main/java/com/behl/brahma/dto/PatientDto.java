package com.behl.brahma.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class PatientDto {

    private final UUID id;
    private final String fullName;

}

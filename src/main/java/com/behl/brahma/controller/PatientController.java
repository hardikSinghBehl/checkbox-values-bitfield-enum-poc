package com.behl.brahma.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.behl.brahma.dto.PatientDto;
import com.behl.brahma.repository.PatientRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PatientController {

    private final PatientRepository patientRepository;

    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientDto>> patientListRetrievalHandler() {
        return ResponseEntity.ok(patientRepository.findAll().parallelStream()
                .map(patient -> PatientDto.builder().id(patient.getId()).fullName(patient.getFullName()).build())
                .collect(Collectors.toList()));
    }

}

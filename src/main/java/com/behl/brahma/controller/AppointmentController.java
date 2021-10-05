package com.behl.brahma.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.behl.brahma.dto.AppointmentCreationRequestDto;
import com.behl.brahma.dto.AppointmentDto;
import com.behl.brahma.entity.Appointment;
import com.behl.brahma.repository.AppointmentRepository;
import com.behl.brahma.repository.PatientRepository;
import com.behl.brahma.utility.BitUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @PostMapping(value = "/appointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> appointmentCreationHandler(
            @RequestBody(required = true) final AppointmentCreationRequestDto appointmentCreationRequestDto) {
        final var appointment = new Appointment();
        final var patient = patientRepository.findById(appointmentCreationRequestDto.getPatientId()).get();
        appointment.setPatient(patient);
        appointment.setSymptoms(BitUtil.getSymptomValue(appointmentCreationRequestDto.getSymptoms()));
        appointment.setPrescription(BitUtil.getPrescriptionValue(appointmentCreationRequestDto.getPrescriptions()));
        final var savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created: {}", savedAppointment);

        final var response = new HashMap<String, String>();
        response.put("message",
                "Appointment created successfully, 'savedObject' contains the .toString() of the stored object");
        response.put("savedObject", savedAppointment.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/appointments/{patientId}")
    public ResponseEntity<List<AppointmentDto>> appointmentListRetreivalHandler(
            @PathVariable(required = true, name = "patientId") final UUID patientId) {
        final var appointments = appointmentRepository.findByPatientId(patientId);
        return ResponseEntity.ok(appointments.parallelStream()
                .map(appointment -> AppointmentDto.builder().id(appointment.getId())
                        .prescriptions(BitUtil.getPrescriptions(appointment.getPrescription()))
                        .symptoms(BitUtil.getSymptoms(appointment.getSymptoms())).build())
                .collect(Collectors.toList()));
    }

}

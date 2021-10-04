package com.behl.brahma.bootstrap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import com.behl.brahma.entity.Patient;
import com.behl.brahma.repository.PatientRepository;
import com.github.javafaker.Faker;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@AllArgsConstructor
@Slf4j
public class PatientDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final PatientRepository patientRepository;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        for (int i = 0; i < 5; i++) {
            final var patient = new Patient();
            patient.setFullName(new Faker().name().fullName());
            patientRepository.save(patient);
        }
        log.info("Added dummy patient data in the system");
    }

}

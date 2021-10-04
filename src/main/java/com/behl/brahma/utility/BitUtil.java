package com.behl.brahma.utility;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.behl.brahma.constant.Prescription;
import com.behl.brahma.constant.Symptom;

public class BitUtil {

    public static EnumSet<Symptom> getSymptoms(final Integer symptomValue) {
        EnumSet<Symptom> symptoms = EnumSet.noneOf(Symptom.class);
        Arrays.asList(Symptom.values()).forEach(symptom -> {
            Integer currentIterationSymptomBitValue = symptom.getBitFlagValue();
            if ((currentIterationSymptomBitValue & symptomValue) == currentIterationSymptomBitValue)
                symptoms.add(symptom);
        });
        return symptoms;
    }

    public static EnumSet<Prescription> getPrescriptions(final Integer prescriptionValue) {
        EnumSet<Prescription> prescriptions = EnumSet.noneOf(Prescription.class);
        Arrays.asList(Prescription.values()).forEach(prescription -> {
            Integer currentIterationPrescriptionBitValue = prescription.getBitFlagValue();
            if ((currentIterationPrescriptionBitValue & prescriptionValue) == currentIterationPrescriptionBitValue)
                prescriptions.add(prescription);
        });
        return prescriptions;
    }

    public static Integer getSymptomValue(Set<Symptom> symptoms) {
        final var value = new AtomicInteger(0);
        symptoms.forEach(symptom -> {
            value.addAndGet(symptom.getBitFlagValue());
        });
        return value.get();
    }

    public static Integer getPrescriptionValue(Set<Prescription> prescriptions) {
        final var value = new AtomicInteger(0);
        prescriptions.forEach(prescription -> {
            value.addAndGet(prescription.getBitFlagValue());
        });
        return value.get();
    }

}

package com.behl.brahma.utility;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
        List symptomList = List.of(symptoms.toArray());
        Integer value = 0;
        for (int i = 0; i < symptomList.size(); i++) {
            value += ((Symptom) symptomList.get(i)).getBitFlagValue();
        }
        return value;
    }

    public static Integer getPrescriptionValue(Set<Prescription> prescriptions) {
        List prescriptionList = List.of(prescriptions.toArray());
        Integer value = 0;
        for (int i = 0; i < prescriptionList.size(); i++) {
            value += ((Prescription) prescriptionList.get(i)).getBitFlagValue();
        }
        return value;
    }

}

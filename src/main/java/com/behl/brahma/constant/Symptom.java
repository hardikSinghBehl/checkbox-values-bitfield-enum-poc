package com.behl.brahma.constant;

public enum Symptom {

    FEVER, COUGH, HEADACHE, EYE_ACHE, COLD;

    public Integer getBitFlagValue() {
        return 1 << this.ordinal();
    }

}

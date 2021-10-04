package com.behl.brahma.constant;

public enum Prescription {

    BUDWEISER, KINGFISHER, BLENDERS_PRIDE,
    ROYAL_STAG, PATANALI_AAMRAS, 
    PATANJALI_GILOY_AMLA;

    public Integer getBitFlagValue() {
        return 1 << this.ordinal();
    }

}

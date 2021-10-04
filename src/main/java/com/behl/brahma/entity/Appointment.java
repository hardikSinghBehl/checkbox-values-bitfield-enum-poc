package com.behl.brahma.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "appointments")
@Data
public class Appointment implements Serializable {

    private static final long serialVersionUID = 467606135076514012L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false)
    private Integer symptoms;

    @Column(nullable = false)
    private Integer prescription;

    @Column(name = "patient_id", nullable = false, insertable = false, updatable = false)
    private UUID patientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @PrePersist
    void onCreate() {
        this.id = UUID.randomUUID();
    }

}

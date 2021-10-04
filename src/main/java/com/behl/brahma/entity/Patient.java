package com.behl.brahma.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "patients")
@Data
public class Patient implements Serializable {

    private static final long serialVersionUID = 7195263729676475679L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @PrePersist
    void onCreate() {
        this.id = UUID.randomUUID();
    }

}

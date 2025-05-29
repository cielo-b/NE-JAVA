package com.app.NE.models;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.N;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payroll")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayRoll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = true)
    private int month;
    @Column(nullable = true)
    private int year;

    @OneToMany
    private List<PaySlip> slips;
}

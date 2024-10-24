package com._tcapital.centronotificaciones.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Addressee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String trackingId;

    private String process;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id")
    private Email email;


}

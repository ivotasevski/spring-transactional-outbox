package com.ivotasevski.transoutbox.lib.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payload;

    private String type;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Integer retries = 0;

    private Instant createdAt;
    private Instant updatedAt;

    @Column(name = "next_run_not_before")
    private Instant nextRunNotBefore;
    private Instant deleteAfter;

    @Column(name = "custom_version")
    private Long customVersion = 1l;
}

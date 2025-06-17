package com.branacar.stock.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity @Table(name = "dealers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dealer {
    @Id
    private UUID dealerId;

    @Column(nullable = false)
    private String name;

    private String address;
}
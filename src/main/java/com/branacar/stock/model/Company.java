package com.branacar.stock.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity @Table(name = "companies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Company {
    @Id
    private UUID companyId;

    @Column(nullable = false)
    private String name;
}
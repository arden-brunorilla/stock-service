package com.branacar.stock.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "stock_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public abstract class Stock {
    @Id
    private UUID stockId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_type", insertable = false, updatable = false)
    private StockType type;

    private String address;
}


package com.branacar.stock.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity @DiscriminatorValue("LOCAL")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class StockLocal extends Stock {

    @Column(name = "dealer_id")
    private UUID dealerId;
}

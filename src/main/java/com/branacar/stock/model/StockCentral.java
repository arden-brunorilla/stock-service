package com.branacar.stock.model;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity @DiscriminatorValue("CENTRAL")
@Getter @Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class StockCentral extends Stock { }
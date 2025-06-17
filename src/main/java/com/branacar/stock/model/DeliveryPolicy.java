package com.branacar.stock.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryPolicy {
    @Id
    private UUID policyId;

    @Enumerated(EnumType.STRING)
    private StockType source;

    private int days;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;
}
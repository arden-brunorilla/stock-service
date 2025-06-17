package com.branacar.stock.controller.dto;


import com.branacar.stock.model.MovementReason;
import com.branacar.stock.model.StockMovement;

import java.time.Instant;
import java.util.UUID;

public record StockMovementDto(
        UUID moveId,
        Instant dateTime,
        MovementReason reason,
        UUID carId,
        UUID originId,
        UUID destinationId
) {
    public static StockMovementDto from(StockMovement m) {
        return new StockMovementDto(
                m.getMoveId(),
                m.getDateTime(),
                m.getReason(),
                m.getCarId(),
                m.getOrigin().getStockId(),
                m.getDestination().getStockId());
    }
}
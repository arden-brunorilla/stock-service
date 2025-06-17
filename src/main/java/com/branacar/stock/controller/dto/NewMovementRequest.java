package com.branacar.stock.controller.dto;


import com.branacar.stock.model.MovementReason;
import java.util.UUID;

public record NewMovementRequest(
        UUID carId,
        UUID originId,
        UUID destinationId,
        MovementReason reason
) { }
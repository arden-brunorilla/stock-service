package com.branacar.stock.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CarSummaryDto(
        UUID carId,
        String vin,
        String status,
        BigDecimal listPrice,
        String modelName,
        String brandName,
        UUID stockId
) { }
package com.branacar.stock.controller.dto;


import com.branacar.stock.model.Stock;
import com.branacar.stock.model.StockType;

import java.util.UUID;


public record StockDto(
        UUID stockId,
        StockType type,
        String address
) {
    public static StockDto from(Stock s) {
        return new StockDto(
                s.getStockId(),
                s.getType(),
                s.getAddress()
        );
    }
}
package com.branacar.stock.service;

import com.branacar.stock.controller.dto.*;

import java.util.List;
import java.util.UUID;

public interface IStockService {
    List<StockDto> listStocks();
    StockDto getCentralStock();
    List<StockDto> getLocalStocks();
    StockStatisticsDto getStockStatistics();
    List<CarSummaryDto> inventory(UUID stockId);
    StockMovementDto move(NewMovementRequest req);
    StockMovementDto reserveCarForSale(UUID carId, UUID centralStockId, UUID localStockId);
    StockDto getStockByCarId(UUID carId);
}
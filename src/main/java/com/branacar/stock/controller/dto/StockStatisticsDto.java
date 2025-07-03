package com.branacar.stock.controller.dto;

public record StockStatisticsDto (
    long centralStockCount,
    long localStockCount
){}

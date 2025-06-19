package com.branacar.stock.service;

import com.branacar.stock.client.CarCatalogClient;
import com.branacar.stock.controller.dto.CarSummaryDto;
import com.branacar.stock.controller.dto.NewMovementRequest;
import com.branacar.stock.controller.dto.StockDto;
import com.branacar.stock.controller.dto.StockMovementDto;
import com.branacar.stock.model.*;
import com.branacar.stock.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepo;
    private final StockMovementRepository movRepo;
    private final CarCatalogClient carClient;

    public List<StockDto> listStocks() {
        return stockRepo.findAll().stream().map(StockDto::from).toList();
    }

    public List<CarSummaryDto> inventory(UUID stockId) {
        return carClient.findByStock(stockId);
    }

    @Transactional
    public StockMovementDto move(NewMovementRequest req) {

        Stock origin = stockRepo.findById(req.originId())
                .orElseThrow(() -> new IllegalArgumentException("Origin not found"));
        Stock dest = stockRepo.findById(req.destinationId())
                .orElseThrow(() -> new IllegalArgumentException("Destination not found"));



        StockMovement mov = movRepo.save(
                StockMovement.builder()
                        .moveId(UUID.randomUUID())
                        .dateTime(Instant.now())
                        .reason(req.reason())
                        .carId(req.carId())
                        .origin(origin)
                        .destination(dest)
                        .build());

        carClient.updateLocation(req.carId(), req.destinationId());

        return StockMovementDto.from(mov);
    }
}
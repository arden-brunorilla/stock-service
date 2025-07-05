package com.branacar.stock.controller;

import com.branacar.stock.controller.dto.*;
import com.branacar.stock.service.IStockService;
import com.branacar.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final IStockService svc;

    /* GET /stocks */
    @GetMapping
    public List<StockDto> list() {
        return svc.listStocks();
    }

    /* GET /stocks/central */
    @GetMapping("/central")
    public StockDto getCentralStock() {
        return svc.getCentralStock();
    }

    /* GET /stocks/local */
    @GetMapping("/local")
    public List<StockDto> getLocalStocks() {
        return svc.getLocalStocks();
    }

    /* GET /stocks/statistics */
    @GetMapping("/statistics")
    public StockStatisticsDto getStatistics() {
        return svc.getStockStatistics();
    }

    /* GET /stocks/{id}/inventory */
    @GetMapping("/{id}/inventory")
    public List<CarSummaryDto> inventory(@PathVariable UUID id) {
        return svc.inventory(id);
    }

    /* POST /stocks/movements */
    @PostMapping("/movements")
    public ResponseEntity<StockMovementDto> move(@RequestBody NewMovementRequest body) {
        return ResponseEntity.ok( svc.move(body) );
    }

    /* POST /stocks/reserve */
    @PostMapping("/reserve")
    public ResponseEntity<StockMovementDto> reserveCar(
            @RequestParam UUID carId,
            @RequestParam UUID centralStockId,
            @RequestParam UUID localStockId) {
        return ResponseEntity.ok(svc.reserveCarForSale(carId, centralStockId, localStockId));
    }

    /* GET /stocks/car/{carId}/location */
    @GetMapping("/car/{carId}/location")
    public ResponseEntity<StockDto> getCarLocation(@PathVariable UUID carId) {
        return ResponseEntity.ok(svc.getStockByCarId(carId));
    }
}

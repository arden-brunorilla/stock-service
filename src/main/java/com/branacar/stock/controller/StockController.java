package com.branacar.stock.controller;

import com.branacar.stock.controller.dto.*;
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

    private final StockService svc;

    /* GET /stocks */
    @GetMapping
    public List<StockDto> list() {
        return svc.listStocks();
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
}

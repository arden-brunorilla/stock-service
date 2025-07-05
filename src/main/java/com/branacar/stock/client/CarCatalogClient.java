package com.branacar.stock.client;

import com.branacar.stock.controller.dto.CarSummaryDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "car-service")
public interface CarCatalogClient {

    @CircuitBreaker(name = "car-service")
    @GetMapping("/cars")
    List<CarSummaryDto> getAllCars();

    @CircuitBreaker(name = "car-service")
    @GetMapping("/cars/stock/{stockId}")
    List<CarSummaryDto> findByStock(@PathVariable UUID stockId);

    @PutMapping("/cars/{id}/location/{stockId}")
    void updateLocation(@PathVariable UUID id,
                        @PathVariable UUID stockId);
}

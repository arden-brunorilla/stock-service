package com.branacar.stock.service;
import com.branacar.stock.controller.dto.StockStatisticsDto;
import com.branacar.stock.client.CarCatalogClient;
import com.branacar.stock.controller.dto.CarSummaryDto;
import com.branacar.stock.controller.dto.NewMovementRequest;
import com.branacar.stock.controller.dto.StockDto;
import com.branacar.stock.controller.dto.StockMovementDto;
import com.branacar.stock.model.*;
import com.branacar.stock.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService implements IStockService{

    private final StockRepository stockRepo;
    private final StockMovementRepository movRepo;
    private final CarCatalogClient carClient;

    public StockDto getCentralStock(){
        return stockRepo.findAll().stream()
            .filter(stock -> stock.getType() == StockType.CENTRAL)
            .findFirst()
            .map(StockDto::from)
            .orElseThrow(() -> new IllegalStateException("Central stock not found"));
    }

    public List<StockDto> getLocalStocks(){
        return stockRepo.findAll().stream()
            .filter(stock -> stock.getType() == StockType.LOCAL)
            .map(StockDto::from)
            .toList();      
    }

    @Transactional
    public StockMovementDto move(NewMovementRequest req){
        Stock origin = stockRepo.findById(req.originId())
                .orElseThrow(() -> new IllegalArgumentException("Origin stock not found"));
        Stock dest = stockRepo.findById(req.destinationId())
                .orElseThrow(() -> new IllegalArgumentException("Destination stock not found"));           
      
        validateMovement(origin, dest, req.reason());

        StockMovement mov = movRepo.save(
            StockMovement.builder()
                .moveId(UUID.randomUUID())
                .dateTime(Instant.now())
                .reason(req.reason())
                .carId(req.carId())
                .origin(origin)
                .destination(dest)
                .build()
        );

        carClient.updateLocation(req.carId(), req.destinationId());

        return StockMovementDto.from(mov); 
    }

    

    public List<StockDto> listStocks() {
        return stockRepo.findAll().stream().map(StockDto::from).toList();
    }

    public List<CarSummaryDto> inventory(UUID stockId) {
        return carClient.findByStock(stockId);
    }

    private void validateMovement(Stock origin, Stock dest, MovementReason reason){
        if(origin.getStockId().equals(dest.getStockId())){
            throw new IllegalArgumentException("Cannot move car to the same stock");
        }
        
        switch(reason){
            case SALE:
                validateSaleMovement(origin, dest);
                break;
            case TRANSFER:
                validateTransferMovement(origin, dest);
                break;
            case RETURN:
                validateReturnMovement(origin, dest);
                break;
            default:
                throw new IllegalArgumentException("Invalid movement reason: " + reason);
        }

    }

     
      private void validateSaleMovement(Stock origin, Stock dest) {
        // En una venta, el origen debe ser un stock local y el destino puede ser central
        if (origin.getType() != StockType.LOCAL) {
            throw new IllegalArgumentException("Sale origin must be a local stock");
        }
    }

    
    private void validateTransferMovement(Stock origin, Stock dest) {
        // Transferencias solo entre stocks del mismo tipo o central-local
        if (origin.getType() == StockType.LOCAL && dest.getType() == StockType.LOCAL) {
            throw new IllegalArgumentException("Cannot transfer between local stocks");
        }
    }

  
    private void validateReturnMovement(Stock origin, Stock dest) {
        // Retornos van de local a central
        if (origin.getType() != StockType.LOCAL || dest.getType() != StockType.CENTRAL) {
            throw new IllegalArgumentException("Returns must go from local to central stock");
        }
    }

    public StockStatisticsDto getStockStatistics() {
        List<Stock> stocks = stockRepo.findAll();
        
        long centralStockCount = stocks.stream()
                .filter(s -> s.getType() == StockType.CENTRAL)
                .count();
        
        long localStockCount = stocks.stream()
                .filter(s -> s.getType() == StockType.LOCAL)
                .count();

        return new StockStatisticsDto(centralStockCount, localStockCount);
    }

    public StockDto getStockByCarId(UUID carId) {
        // Buscar en qué stock está el auto actualmente
        List<CarSummaryDto> carsInCentral = carClient.findByStock(getCentralStock().stockId());
        boolean isInCentral = carsInCentral.stream()
                .anyMatch(car -> car.carId().equals(carId));
        
        if (isInCentral) {
            return getCentralStock();
        } else {
            // Buscar en stocks locales
            List<StockDto> localStocks = getLocalStocks();
            for (StockDto localStock : localStocks) {
                List<CarSummaryDto> carsInLocal = carClient.findByStock(localStock.stockId());
                boolean isInLocal = carsInLocal.stream()
                        .anyMatch(car -> car.carId().equals(carId));
                if (isInLocal) {
                    return localStock;
                }
            }
        }
        
        throw new IllegalArgumentException("Car not found in any stock");
    }

    @Transactional
    public StockMovementDto reserveCarForSale(UUID carId, UUID centralStockId, UUID localStockId) {
        Stock centralStock = stockRepo.findById(centralStockId)
                .orElseThrow(() -> new IllegalArgumentException("Central stock not found"));
        Stock localStock = stockRepo.findById(localStockId)
                .orElseThrow(() -> new IllegalArgumentException("Local stock not found"));
        
        if (centralStock.getType() != StockType.CENTRAL) {
            throw new IllegalArgumentException("Origin must be central stock");
        }
        if (localStock.getType() != StockType.LOCAL) {
            throw new IllegalArgumentException("Destination must be local stock");
        }

        // Crear movimiento de transferencia programada
        StockMovement mov = movRepo.save(
            StockMovement.builder()
                .moveId(UUID.randomUUID())
                .dateTime(Instant.now())
                .reason(MovementReason.TRANSFER)
                .carId(carId)
                .origin(centralStock)
                .destination(localStock)
                .build()
        );

        // Actualizar ubicación del auto
        carClient.updateLocation(carId, localStockId);

        return StockMovementDto.from(mov);
    }

    // 🛡️ MÉTODOS DE FALLBACK PARA CIRCUIT BREAKERS
    public List<CarSummaryDto> getAllCarsFallback(Exception e) {
        log.error("Fallback: Error getting all cars - {}", e.getMessage());
        throw new IllegalStateException("Car service unavailable - " + e.getMessage());
    }

    public List<CarSummaryDto> findByStockFallback(UUID stockId, Exception e) {
        log.error("Fallback: Error getting cars by stock {} - {}", stockId, e.getMessage());
        throw new IllegalStateException("Car service unavailable - " + e.getMessage());
    }
}
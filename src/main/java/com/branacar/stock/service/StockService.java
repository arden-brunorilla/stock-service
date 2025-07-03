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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

}
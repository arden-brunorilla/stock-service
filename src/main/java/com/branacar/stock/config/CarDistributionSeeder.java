package com.branacar.stock.config;

import com.branacar.stock.client.CarCatalogClient;
import com.branacar.stock.controller.dto.CarSummaryDto;
import com.branacar.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CarDistributionSeeder {

    private final CarCatalogClient carClient;

    private static final UUID STOCK_CENTRAL = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-ffffffffffff");
    private static final UUID STOCK_LOCAL = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    @Bean
    CommandLineRunner distributeCars() {
        return args -> {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            try {
                 List<CarSummaryDto> carsInCentral = carClient.findByStock(STOCK_CENTRAL);
                List<CarSummaryDto> carsInLocal = carClient.findByStock(STOCK_LOCAL);
                
                log.info("Current distribution: Central={} cars, Local={} cars",
                        carsInCentral.size(), carsInLocal.size());

                if (!carsInCentral.isEmpty() && !carsInLocal.isEmpty()) {
                    log.info("Cars already distributed across stocks");
                    return;
                }

                List<CarSummaryDto> allCars = carClient.findByStock(STOCK_CENTRAL);
                
                if (allCars.isEmpty()) {
                    log.warn("No cars found in central stock. Checking local stock...");
                    allCars = carClient.findByStock(STOCK_LOCAL);
                    
                    if (allCars.isEmpty()) {
                        log.error("No cars found in any stock. Make sure car-service is running and has cars.");
                        return;
                    } else {
                        log.info("Found {} cars in local stock. Moving some to central stock...", allCars.size());
                        int carsToMoveToCentral = Math.min(allCars.size() / 2, 2);
                        for (int i = 0; i < carsToMoveToCentral; i++) {
                            CarSummaryDto car = allCars.get(i);
                            try {
                                carClient.updateLocation(car.carId(), STOCK_CENTRAL);
                                log.info("Moved car {} (VIN: {}) to central stock", car.carId(), car.vin());
                                Thread.sleep(500);
                            } catch (Exception e) {
                                log.error("Failed to move car {} to central stock: {}", car.carId(), e.getMessage());
                            }
                        }
                        
                        Thread.sleep(2000);
                        allCars = carClient.findByStock(STOCK_CENTRAL);
                    }
                }

                log.info("Found {} cars in central stock. Distributing...", allCars.size());

                int carsToMove = Math.min(allCars.size() / 2, 3);
                
                if (carsToMove == 0) {
                    log.info("Not enough cars to distribute (need at least 2)");
                    return;
                }

                log.info("Moving {} cars to local stock...", carsToMove);

                for (int i = 0; i < carsToMove; i++) {
                    CarSummaryDto car = allCars.get(i);
                    try {
                        carClient.updateLocation(car.carId(), STOCK_LOCAL);
                        log.info("Moved car {} (VIN: {}) to local stock", car.carId(), car.vin());

                        Thread.sleep(500);
                    } catch (Exception e) {
                        log.error("Failed to move car {} to local stock: {}", car.carId(), e.getMessage());
                    }
                }

                Thread.sleep(2000);
                List<CarSummaryDto> finalCarsInCentral = carClient.findByStock(STOCK_CENTRAL);
                List<CarSummaryDto> finalCarsInLocal = carClient.findByStock(STOCK_LOCAL);
                
                log.info("Car distribution completed!");
                log.info("Central stock: {} cars", finalCarsInCentral.size());
                log.info("Local stock: {} cars", finalCarsInLocal.size());
                
                if (!finalCarsInCentral.isEmpty()) {
                    log.info("Sample car in central: {} ({})",
                            finalCarsInCentral.get(0).vin(), finalCarsInCentral.get(0).modelName());
                }
                if (!finalCarsInLocal.isEmpty()) {
                    log.info("Sample car in local: {} ({})",
                            finalCarsInLocal.get(0).vin(), finalCarsInLocal.get(0).modelName());
                }
                
            } catch (Exception e) {
                log.error("Error during car distribution: {}", e.getMessage());
            }
        };
    }
} 
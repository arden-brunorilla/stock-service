package com.branacar.stock.config;

import com.branacar.stock.model.*;
import com.branacar.stock.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class StockSeeder {

    private final CompanyRepository companyRepo;
    private final StockRepository stockRepo;
    private final DealerRepository dealerRepo;

    @Bean
    CommandLineRunner seedStock() {
        return args -> {
            if (stockRepo.count() > 0) return;

            Company company = companyRepo.save(
                    Company.builder()
                            .companyId(UUID.randomUUID())
                            .name("Branacar Sociedad Anónima")
                            .build());

            // Central
            StockCentral central = (StockCentral) stockRepo.save(
                    StockCentral.builder()
                            .stockId(UUID.randomUUID())
                            .address("Av. Panamerican 874")
                            .build());

            // Concesionaria
            Dealer dealerA = dealerRepo.save(
                    Dealer.builder()
                            .dealerId(UUID.randomUUID())
                            .name("Branacar Jorge Ferro")
                            .address("Av. Libertador 861")
                            .build());

            stockRepo.save(
                    StockLocal.builder()
                            .stockId(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"))
                            .address("Sucursal Centro")
                            .dealerId(dealerA.getDealerId())
                            .build());

            System.out.println("✅ Stock seed loaded");
        };
    }
}
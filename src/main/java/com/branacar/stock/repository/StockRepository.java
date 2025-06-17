package com.branacar.stock.repository;

import com.branacar.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> { }
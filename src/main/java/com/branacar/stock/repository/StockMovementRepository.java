package com.branacar.stock.repository;
import com.branacar.stock.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> { }
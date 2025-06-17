package com.branacar.stock.repository;

import com.branacar.stock.model.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DealerRepository extends JpaRepository<Dealer, UUID> { }
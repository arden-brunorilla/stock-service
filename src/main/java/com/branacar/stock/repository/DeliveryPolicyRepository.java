package com.branacar.stock.repository;

import com.branacar.stock.model.DeliveryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, UUID> { }

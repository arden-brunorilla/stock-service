package com.branacar.stock.repository;

import com.branacar.stock.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> { }
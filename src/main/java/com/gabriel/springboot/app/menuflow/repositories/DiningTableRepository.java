package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

    Optional<DiningTable> findByNumber(Integer number);

    Optional<DiningTable> findByQrCode(String qrCode);

    Boolean existsByNumber(Integer number);

    Boolean existsByQrCode(String qrCode);

    List<DiningTable> findByIsActiveTrue();
}

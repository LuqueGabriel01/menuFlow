package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.TableSession;
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableSessionRepository extends JpaRepository<TableSession, Long> {

    Optional<TableSession> findByDiningTableIdAndStatus(Long tableId, SessionStatus status);

    List<TableSession> findByStatus(SessionStatus status);

    List<TableSession> findByDiningTableId(Long tableId);

    Boolean existsByDiningTableIdAndStatus(Long tableId, SessionStatus status);
}

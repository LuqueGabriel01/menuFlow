package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.DiningTableMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.CreateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.UpdateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.TableResponse;
import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import com.gabriel.springboot.app.menuflow.repositories.DiningTableRepository;
import com.gabriel.springboot.app.menuflow.repositories.TableSessionRepository;
import com.gabriel.springboot.app.menuflow.services.DiningTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository diningTableRepository;
    private final TableSessionRepository sessionRepository;
    private final DiningTableMapper diningTableMapper;

    @Override
    @Transactional
    public TableResponse createTable(CreateTableRequest request) {
        log.info("create table number: {}", request.number());

        if (diningTableRepository.existsByNumber(request.number())) {
            throw new BusinessException(DINING_TABLE_EXISTS_MESSAGE + request.number());
        }

        DiningTable table = DiningTable.of(request.number(), generateUniqueQrCode());

        DiningTable saved = diningTableRepository.save(table);

        log.info("create table successful - ID: {}, Number: {}", saved.getId(), request.number());

        return diningTableMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TableResponse updateTable(Long id,UpdateTableRequest request) {
        log.info("update table number: {}", request.number());

        DiningTable table = diningTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TABLE_NOT_FOUND_MESSAGE));

        if(!table.getNumber().equals(request.number())) {
            checkNumberAvailability(request.number());
            table.assignNumber(request.number());
        }

        updateTableActive(table, request.active());

        DiningTable updated = diningTableRepository.save(table);

        log.info("update table successful - ID: {}, Number: {}", updated.getId(), request.number());

        return diningTableMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TableResponse> getTableById(Long id) {
        return diningTableRepository.findById(id)
                .map(diningTableMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TableResponse> getTableByQrCode(String qrCode) {
        return diningTableRepository.findByQrCode(qrCode)
                .map(diningTableMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableResponse> getAllTables() {
        return diningTableRepository.findAll().stream()
                .map(diningTableMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableResponse> getActiveTables() {
        return diningTableRepository.findByIsActiveTrue().stream()
                .map(diningTableMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteTableById(Long id) {
        log.info("delete table number: {}", id);

        DiningTable table = diningTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TABLE_NOT_FOUND_MESSAGE));

        if(hasActiveSession(id)){
            throw new BusinessException(TABLE_ACTIVE_MESSAGE);
        }

        diningTableRepository.delete(table);

        log.info("delete table successful - ID: {}, Number: {}", id, table.getNumber());
    }

    @Override
    @Transactional
    public TableResponse toggleTableStatus(Long id) {

        DiningTable table =  diningTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TABLE_NOT_FOUND_MESSAGE));

        updateTableActive(table, !table.isActive());

        DiningTable updated = diningTableRepository.save(table);

        return diningTableMapper.toResponse(updated);
    }

    private void updateTableActive(DiningTable table, Boolean active) {
        if (active == null) return;
        if (active) table.enable();
        else table.disable();
    }

    private void checkNumberAvailability(Integer number) {
        if (diningTableRepository.existsByNumber(number)) {
            throw new BusinessException(DINING_TABLE_EXISTS_MESSAGE  + number);
        }
    }

    private boolean hasActiveSession(Long tableId) {
        return sessionRepository.existsByDiningTableIdAndStatus(tableId, SessionStatus.OPEN);
    }

    private String generateUniqueQrCode() {
        String qrCode;
        do {
            qrCode = UUID.randomUUID().toString();
        } while (diningTableRepository.existsByQrCode(qrCode));

        return qrCode;
    }
}

package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.CreateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.UpdateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.TableResponse;

import java.util.List;
import java.util.Optional;

public interface DiningTableService {
    TableResponse createTable(CreateTableRequest request);
    TableResponse updateTable(Long id, UpdateTableRequest request);
    Optional<TableResponse> getTableById(Long id);
    Optional<TableResponse> getTableByQrCode(String qrCode);
    List<TableResponse> getAllTables();
    List<TableResponse> getActiveTables();
    void deleteTableById(Long id);
    TableResponse toggleTableStatus(Long id);
}

package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.CreateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.UpdateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.TableResponse;

import java.util.List;

public interface DiningTableService {
    TableResponse createTable(CreateTableRequest request);
    TableResponse updateTable(Long id, UpdateTableRequest request);
    TableResponse getTableById(Long id);
    TableResponse getTableByQrCode(String qrCode);
    List<TableResponse> getAllTables();
    List<TableResponse> getActiveTables();
    void deleteTableById(Long id);
    TableResponse toggleTableStatus(Long id);
}

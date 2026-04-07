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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiningTableServiceImplTest {

    @Mock
    private DiningTableRepository diningTableRepository;

    @Mock
    private TableSessionRepository sessionRepository;

    @Mock
    private DiningTableMapper diningTableMapper;

    @InjectMocks
    private DiningTableServiceImpl diningTableService;

    @Test
    @DisplayName("Should create table successfully when number does not exist")
    void createTable() {

        CreateTableRequest request = new CreateTableRequest(10,true);

        DiningTable table = DiningTable.of(10, "qr-123");
        TableResponse response = new TableResponse(1L, 10, true,"qr-123",null,true);

        when(diningTableRepository.existsByNumber(10)).thenReturn(false);
        when(diningTableRepository.existsByQrCode(anyString())).thenReturn(false);
        when(diningTableRepository.save(any(DiningTable.class))).thenReturn(table);
        when(diningTableMapper.toResponse(table)).thenReturn(response);

        TableResponse result = diningTableService.createTable(request);

        assertNotNull(result);
        assertEquals(10, result.number());
        verify(diningTableRepository).save(any(DiningTable.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when creating table with existing number")
    void createTableNoExist() {
        CreateTableRequest request = new CreateTableRequest(10,true);

        when(diningTableRepository.existsByNumber(10)).thenReturn(true);

        assertThrows(BusinessException.class, () -> diningTableService.createTable(request));

        verify(diningTableRepository, never()).save(any(DiningTable.class));
    }

    @Test
    @DisplayName("Should delete table successfully when no active session exists")
    void deleteTable() {
        DiningTable table = DiningTable.of(10, "qr");

        when(diningTableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(sessionRepository.existsByDiningTableIdAndStatus(1L, SessionStatus.OPEN))
                .thenReturn(false);

        diningTableService.deleteTableById(1L);

        verify(diningTableRepository).delete(table);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting table with active session")
    void deleteTableError() {
        DiningTable table = DiningTable.of(10, "qr");

        when(diningTableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(sessionRepository.existsByDiningTableIdAndStatus(1L, SessionStatus.OPEN))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> diningTableService.deleteTableById(1L));

        verify(diningTableRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent table")
    void deleteTableNotFound() {
        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> diningTableService.deleteTableById(1L));
    }

    @Test
    @DisplayName("Should update table successfully when table exists and number is available")
    void updateTable() {
        DiningTable table = DiningTable.of(10, "qr");

        UpdateTableRequest request = new UpdateTableRequest(20, true);

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        when(diningTableRepository.existsByNumber(20))
                .thenReturn(false);

        when(diningTableRepository.save(table))
                .thenReturn(table);

        when(diningTableMapper.toResponse(table))
                .thenReturn(new TableResponse(1L, 20, true, "qr", null, true));

        TableResponse result = diningTableService.updateTable(1L, request);

        assertEquals(20, result.number());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent table")
    void updateTableNotFound() {
        UpdateTableRequest request = new UpdateTableRequest(20, true);

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> diningTableService.updateTable(1L, request));
    }

    @Test
    @DisplayName("Should throw BusinessException when updating table with existing number")
    void updateTableExists() {
        DiningTable table = DiningTable.of(10, "qr");
        UpdateTableRequest request = new UpdateTableRequest(20, true);

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        when(diningTableRepository.existsByNumber(20))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> diningTableService.updateTable(1L, request));
    }

    @Test
    @DisplayName("Should toggle table status successfully when table exists")
    void toggleTableStatus() {
        DiningTable table = DiningTable.of(10, "qr");

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        when(diningTableRepository.save(table))
                .thenReturn(table);

        when(diningTableMapper.toResponse(table))
                .thenReturn(new TableResponse(1L, 10, false, "qr", null, true));

        TableResponse result = diningTableService.toggleTableStatus(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should return TableResponse when table exists")
    void getTableById() {
        DiningTable table = DiningTable.of(10, "qr");

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        when(diningTableMapper.toResponse(table))
                .thenReturn(new TableResponse(1L, 10, true, "qr", null, true));

        TableResponse result =
                diningTableService.getTableById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(10, result.number());
        assertEquals("qr", result.qrCode());

        verify(diningTableRepository).findById(1L);
        verify(diningTableMapper).toResponse(table);
    }
}
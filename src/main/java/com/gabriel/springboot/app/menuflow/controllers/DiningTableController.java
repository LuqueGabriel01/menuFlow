package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.CreateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.UpdateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.TableResponse;
import com.gabriel.springboot.app.menuflow.services.DiningTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.*;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.DELETE_SUCCESS_MESSAGE;

@Slf4j
@RestController
@RequestMapping(TABLE_PATH)
@Tag(name = "Table Management", description = "Endpoints for restaurant table management")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class DiningTableController {

    private final DiningTableService diningTableService;

    @GetMapping
    @Operation(
            summary = "List of all tables",
            description = "Get the full list of tables, whether active or inactive"
    )
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAllTables() {
        log.info("GET: {} - get list of tables", TABLE_PATH);

        List<TableResponse> tables = diningTableService.getAllTables();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(tables));
    }

    @GetMapping(ACTIVE)
    @Operation(
            summary = "List active tables",
            description = "Retrieves only the tables marked as active"
    )
    public ResponseEntity<ApiResponse<List<TableResponse>>> getActiveTables() {
        log.info("GET: {} - get list of active tables",  TABLE_PATH + ACTIVE);

        List<TableResponse> tables = diningTableService.getActiveTables();

        return ResponseEntity.ok().body(ApiResponse.success(tables));
    }

    @GetMapping(PATH_ID)
    @Operation(
            summary = "Get table by ID"
    )
    public ResponseEntity<ApiResponse<TableResponse>> getTable(@PathVariable Long id) {
        log.info("GET: {}/{} - get table with id", TABLE_PATH, id);

        TableResponse response = diningTableService.getTableById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @GetMapping(PATH_QR_CODE)
    @Operation(
            summary = "Search for a table using a QR code",
            description = "Search for a table using its unique QR code"
    )
    public ResponseEntity<ApiResponse<TableResponse>> getTableByQrCode(@PathVariable String qrCode) {
        log.info("GET: {}/qr/{} - get table with qrCode}", TABLE_PATH, qrCode);

        TableResponse response = diningTableService.getTableByQrCode(qrCode);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(
            summary = "Create tables",
            description = "Create a new table in system. ADMIN only"
    )
    public ResponseEntity<ApiResponse<TableResponse>> createTable(@Valid @RequestBody CreateTableRequest request) {
        log.info("POST: {} - create table: {}", TABLE_PATH, request.number());

        TableResponse tableResponse = diningTableService.createTable(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tableResponse));
    }

    @PutMapping(PATH_ID)
    @Operation(
            summary = "Update tables"
    )
    public ResponseEntity<ApiResponse<TableResponse>> updateTable(@PathVariable Long id, @Valid @RequestBody UpdateTableRequest request) {
        log.info("PUT: {}/{} - update table: {}", TABLE_PATH, id, request.number());

        TableResponse tableResponse = diningTableService.updateTable(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(tableResponse));
    }

    @PatchMapping(PATH_ID_TOGGLE)
    @Operation(
            summary = "Change table status",
            description = "Toggle between active and inactive"
    )
    public ResponseEntity<ApiResponse<TableResponse>> toggleTableStatus(@PathVariable Long id) {
        log.info("GET: {}/{} - toggle table status", TABLE_PATH, id);

        TableResponse tableResponse = diningTableService.toggleTableStatus(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(tableResponse));
    }

    @DeleteMapping(PATH_ID)
    @Operation(
            summary = "Delete table",
            description = "Delete a table. You cannot delete it if you are currently logged in."
    )
    public ResponseEntity<ApiResponse<Void>> deleteTable(@PathVariable Long id) {
        log.info("DELETE: {}/{} - delete table", TABLE_PATH, id);

        diningTableService.deleteTableById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(DELETE_SUCCESS_MESSAGE, null));
    }
}

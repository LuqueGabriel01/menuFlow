package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.constants.ApiPaths;
import com.gabriel.springboot.app.menuflow.models.dto.request.table.CloseSessionRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.table.SessionDetailResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.table.SessionResponse;
import com.gabriel.springboot.app.menuflow.services.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPaths.Session.SESSIONS)
@RequiredArgsConstructor
@Tag(name = "Session Management", description = "Endpoints for desktop session management")
@SecurityRequirement(name = "Bearer Authentication")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping(ApiPaths.Session.OPEN)
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Open a table session",
            description = "Creates a new active session for a table. ADMIN and CASHIER only."
    )
    public ResponseEntity<ApiResponse<SessionResponse>> openSession(
            @RequestParam Long tableId,
            @RequestParam(required = false) Long openedBy
    ) {
        log.info("POST /api/sessions/open - Mesa: {}, open by: {}", tableId, openedBy);

        SessionResponse response = sessionService.openSession(tableId, openedBy);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Session successfully opened", response));
    }

    @PostMapping(ApiPaths.Session.CLOSE)
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Close table session",
            description = "Closes an active session. ADMIN and CASHIER only."
    )
    public ResponseEntity<ApiResponse<SessionResponse>> closeSession(
            @Valid @RequestBody CloseSessionRequest request
    ) {
        log.info("POST /api/sessions/close - session: {}", request.sessionId());

        SessionResponse response = sessionService.closeSession(request);

        return ResponseEntity.ok(
                ApiResponse.success("session successfully", response)
        );
    }

    @GetMapping(ApiPaths.Session.ACTIVE)
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER', 'KITCHEN')")
    @Operation(
            summary = "List active sessions",
            description = "Retrieves all sessions with the OPEN status"
    )
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getActiveSessions() {
        log.info("GET /api/sessions/active - List active sessions");

        List<SessionResponse> sessions = sessionService.getAllActiveSessions();

        return ResponseEntity.ok(
                ApiResponse.success("Active sessions obtained", sessions)
        );
    }

    @GetMapping(ApiPaths.Session.ID)
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<ApiResponse<SessionResponse>> getSessionById(
            @PathVariable Long id
    ) {
        log.info("GET /api/sessions/{} - Get session", id);

        SessionResponse session = sessionService.getSessionById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Session obtained", session)
        );
    }

    @GetMapping(ApiPaths.Session.ID_DETAILS)
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Get session details",
            description = "Retrieves a session containing the order list and total amount"
    )
    public ResponseEntity<ApiResponse<SessionDetailResponse>> getSessionDetails(
            @PathVariable Long id
    ) {
        log.info("GET /api/sessions/{}/details - Get details ", id);

        SessionDetailResponse details = sessionService.getSessionDetail(id);

        return ResponseEntity.ok(
                ApiResponse.success("Session details retrieved", details)
        );
    }

    @GetMapping(ApiPaths.Session.TABLE_TABLE_ID)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Session history by table",
            description = "Retrieves all sessions (open and closed) for a table"
    )
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessionsByTable(
            @PathVariable Long tableId
    ) {
        log.info("GET /api/sessions/table/{} - Table history", tableId);

        List<SessionResponse> sessions = sessionService.getSessionsByTable(tableId);

        return ResponseEntity.ok(
                ApiResponse.success("Session history retrieved", sessions)
        );
    }

    @GetMapping(ApiPaths.Session.TABLE_TABLE_ID_ACTIVE)
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Active session on a table",
            description = "Retrieves the OPEN session for a specific table"
    )
    public ResponseEntity<ApiResponse<SessionResponse>> getActiveSessionByTable(
            @PathVariable Long tableId
    ) {
        log.info("GET /api/sessions/table/{}/active - Active session", tableId);

        SessionResponse session = sessionService.getActivateSessionByTable(tableId);

        if (session != null) {
            return ResponseEntity.ok(
                    ApiResponse.success("Active session found", session)
            );
        } else {
            return ResponseEntity.ok(
                    ApiResponse.success("There is no active session for this table", null)
            );
        }
    }
}

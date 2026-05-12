package com.gabriel.springboot.app.menuflow.models.dto.request.tableManagement;

import jakarta.validation.constraints.NotNull;

public record CloseSessionRequest(
        @NotNull
        Long sessionId,
        Long closedBy
) {
}

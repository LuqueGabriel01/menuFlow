package com.gabriel.springboot.app.menuflow.models.dto.request.tableManagement;

import jakarta.validation.constraints.NotBlank;

public record TableLoginRequest(
        @NotBlank(message = "QR is required")
        String qrCode
) {
}

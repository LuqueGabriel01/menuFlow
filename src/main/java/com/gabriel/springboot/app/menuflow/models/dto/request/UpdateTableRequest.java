package com.gabriel.springboot.app.menuflow.models.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateTableRequest(
        @NotNull
        Integer number,
        @NotNull
        Boolean active
) {
}

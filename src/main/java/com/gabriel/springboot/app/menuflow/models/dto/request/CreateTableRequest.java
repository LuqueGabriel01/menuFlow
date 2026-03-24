package com.gabriel.springboot.app.menuflow.models.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateTableRequest(

        @NotNull
        @Min(value = 1)
        Integer number,
        Boolean active
) {
    public CreateTableRequest {
        if (active == null) {
            active = true;
        }
    }
}

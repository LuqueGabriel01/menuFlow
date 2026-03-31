package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.TableResponse;
import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import org.springframework.stereotype.Component;

@Component
public class DiningTableMapper {
    public TableResponse toResponse(DiningTable diningTable) {
        return TableResponse.builder()
                .id(diningTable.getId())
                .number(diningTable.getNumber())
                .active(diningTable.isActive())
                .qrCode(diningTable.getQrCode())
                .build();
    }
}

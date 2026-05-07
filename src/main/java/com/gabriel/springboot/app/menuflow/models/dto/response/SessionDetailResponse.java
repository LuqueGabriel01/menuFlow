package com.gabriel.springboot.app.menuflow.models.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record SessionDetailResponse (
    SessionResponse session,
    List<OrderSummaryResponse> orders,
    BigDecimal totalAmount,
    Integer orderCount
) {
}

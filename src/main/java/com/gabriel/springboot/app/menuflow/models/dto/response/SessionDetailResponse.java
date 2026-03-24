package com.gabriel.springboot.app.menuflow.models.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SessionDetailResponse (
    SessionResponse session,
    List<OrderSummaryResponse> orders,
    BigDecimal totalAmount,
    Integer orderCount
) {
}

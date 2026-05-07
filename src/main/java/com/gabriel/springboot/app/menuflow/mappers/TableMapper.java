package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.OrderSummaryResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.SessionDetailResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.SessionResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.TableSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TableMapper {

    private final OrderMapper orderMapper;

    public SessionResponse convertToSession(TableSession session) {
        SessionResponse.SessionResponseBuilder builder = SessionResponse.builder()
                .id(session.getId())
                .tableId(session.getDiningTable().getId())
                .tableNumber(session.getDiningTable().getNumber())
                .openedAt(session.getOpenedAt())
                .closedAt(session.getClosedAt())
                .status(session.getStatus())
                .duration(calculateDuration(session.getOpenedAt(), session.getClosedAt()));

        if(session.getOpenedBy() != null) {
            builder.openedBy(session.getOpenedBy().getUsername());
        }

        return builder.build();
    }

    public SessionDetailResponse convertToDetailResponse(TableSession session, List<Order> orders) {

        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OrderSummaryResponse> orderSummaries = orders.stream()
                .map(orderMapper::convertToOrderSummary)
                .toList();

        SessionResponse sessionDetail = convertToSession(session);

        SessionDetailResponse.SessionDetailResponseBuilder builder = SessionDetailResponse.builder()
                .session(sessionDetail)
                .orders(orderSummaries)
                .totalAmount(totalAmount)
                .orderCount(orders.size());

        return builder.build();
    }

    public String calculateDuration(LocalDateTime openedAt, LocalDateTime closedAt) {
        LocalDateTime end = closedAt != null ? closedAt : LocalDateTime.now();
        Duration duration = Duration.between(openedAt, end);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "min";
        } else {
            return minutes + "min";
        }
    }
}

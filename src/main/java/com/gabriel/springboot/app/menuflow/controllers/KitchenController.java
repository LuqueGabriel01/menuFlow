package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;
import com.gabriel.springboot.app.menuflow.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.KITCHEN_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.QUEUE;

@Slf4j
@RestController
@RequestMapping(KITCHEN_PATH)
@Tag(name = "Kitchen", description = "Kitchen display: what needs to be cooked, in arrival order")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class KitchenController {

    private final OrderService orderService;

    @GetMapping(QUEUE)
    @Operation(
            summary = "Kitchen queue",
            description = "Orders that still need to be cooked (PENDING, IN_PROGRESS), oldest first. " +
                    "READY orders are no longer kitchen's responsibility - they wait to be served. " +
                    "ADMIN and KITCHEN only."
    )
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getQueue() {
        log.info("GET: {} - get kitchen queue", KITCHEN_PATH + QUEUE);

        List<OrderResponse> orders = orderService.getKitchenQueue();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(orders));
    }
}

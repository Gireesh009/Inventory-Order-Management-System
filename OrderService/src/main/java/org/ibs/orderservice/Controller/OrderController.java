package org.ibs.orderservice.Controller;

import lombok.RequiredArgsConstructor;
import org.ibs.orderservice.dto.OrderRequest;
import org.ibs.orderservice.dto.OrderResponse;
import org.ibs.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place-order")
    public ResponseEntity<OrderResponse>
    placeOrder(@RequestBody OrderRequest request,@RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(
                orderService.placeOrder(request,token));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<OrderResponse>>
    getOrders(@PathVariable Long customerId) {

        return ResponseEntity.ok(
                orderService.getCustomerOrders(customerId));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token
    ) {

        orderService.cancelOrder(orderId, token);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order cancelled successfully");
        response.put("orderId", orderId);
        response.put("status", "CANCELLED");

        return ResponseEntity.ok(response);
    }
}
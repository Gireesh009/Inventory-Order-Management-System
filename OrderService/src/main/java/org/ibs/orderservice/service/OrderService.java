package org.ibs.orderservice.service;

import org.ibs.orderservice.Exceptions.InsufficientStockException;
import lombok.RequiredArgsConstructor;
import org.ibs.orderservice.Exceptions.OrderNotFoundException;
import org.ibs.orderservice.entity.OrderItem;
import org.ibs.orderservice.dto.*;
import org.ibs.orderservice.entity.Order;
import org.ibs.orderservice.entity.OrderStatus;
import org.ibs.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository repository;
    private final InventoryClientService inventoryClientService;

    public OrderResponse placeOrder(
            OrderRequest request, String token) {

        double total = 0;

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            // GET PRODUCT DETAILS CALL TO PRODUCT SERVICE

            ProductResponse product =
                    inventoryClientService.getProduct(itemRequest.getProductId());
            order.setCurrency(product.getCurrency());
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock");
            }

            // REDUCE STOCK CALL
            inventoryClientService.reduceStock(
                    product.getId(),
                    itemRequest.getQuantity(),token
            );

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setCurrency(product.getCurrency());
            item.setQuantity(itemRequest.getQuantity());
            item.setOrder(order);

            items.add(item);

            total += product.getPrice() * itemRequest.getQuantity();

        }

        order.setItems(items);
        order.setTotalAmount(total);


        Order saved = repository.save(order);

        return mapToResponse(saved);
    }

    public List<OrderResponse>
    getCustomerOrders(Long customerId) {

        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses =
                order.getItems()
                        .stream()
                        .map(item -> OrderItemResponse.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .price(item.getPrice())
                                .currency(item.getCurrency())
                                .quantity(item.getQuantity())
                                .build())
                        .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    @Transactional
    public void cancelOrder(Long orderId, String token) {

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));



        // restore stock
        for (OrderItem item : order.getItems()) {

            inventoryClientService.increaseStock(
                    item.getProductId(),
                    item.getQuantity(),
                    token
            );
        }

        // update status
        order.setStatus(OrderStatus.CANCELLED);

        repository.save(order);
    }
}
package org.ibs.orderservice.service;

import org.ibs.orderservice.dto.*;
import org.ibs.orderservice.entity.*;
import org.ibs.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private InventoryClientService inventoryClientService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldPlaceOrderSuccessfully() {

        String token = "Bearer abc";

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(100L);
        request.setItems(List.of(itemRequest));

        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(500.0);
        product.setStock(10);

        when(inventoryClientService.getProduct(1L)).thenReturn(product);

        when(repository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.placeOrder(request, token);

        assertNotNull(response);
        assertEquals(100L, response.getCustomerId());
        assertEquals(1, response.getItems().size());

        verify(inventoryClientService).getProduct(1L);
        verify(inventoryClientService).reduceStock(1L, 2, token);
        verify(repository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {

        String token = "Bearer abc";

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(10);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(100L);
        request.setItems(List.of(itemRequest));

        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setStock(2);

        when(inventoryClientService.getProduct(1L)).thenReturn(product);

        assertThrows(RuntimeException.class,
                () -> orderService.placeOrder(request, token));

        verify(inventoryClientService).getProduct(1L);
        verify(inventoryClientService, never()).reduceStock(any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldCancelOrderSuccessfully() {

        String token = "Bearer abc";

        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);

        Order order = new Order();
        order.setId(1L);
        order.setItems(List.of(item));
        order.setStatus(OrderStatus.CREATED);

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, token);

        verify(inventoryClientService).increaseStock(1L, 2, token);
        verify(repository).save(order);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(1L, "token"));
    }
}
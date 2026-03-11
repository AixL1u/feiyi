package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.feiyi.common.Result;
import com.example.feiyi.entity.Order;
import com.example.feiyi.entity.OrderItem;
import com.example.feiyi.entity.Product;
import com.example.feiyi.mapper.OrderItemMapper;
import com.example.feiyi.mapper.OrderMapper;
import com.example.feiyi.mapper.ProductMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @PostMapping("/create")
    public Result<?> create(@RequestBody CreateOrderRequest request, Authentication auth) {
        if (auth == null) {
            return Result.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus(0);
        order.setAddressInfo(request.getAddressInfo());
        order.setRemark(request.getRemark());

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.CartItem item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                return Result.error("商品 " + (product == null ? "" : product.getName()) + " 库存不足");
            }
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalAmount(total);
        orderMapper.insert(order);

        for (CreateOrderRequest.CartItem item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setProductId(product.getId());
            oi.setProductName(product.getName());
            oi.setProductCover(product.getCoverUrl());
            oi.setPrice(product.getPrice());
            oi.setQuantity(item.getQuantity());
            oi.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItemMapper.insert(oi);

            product.setStock(product.getStock() - item.getQuantity());
            product.setSales(product.getSales() + item.getQuantity());
            productMapper.updateById(product);
        }

        return Result.success(order);
    }

    @GetMapping("/my")
    public Result<?> myOrders(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Authentication auth) {
        if (auth == null) {
            return Result.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        Page<Order> p = new Page<>(page, size);
        orderMapper.selectPage(p, new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime));
        for (Order o : p.getRecords()) {
            o.setItems(orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId())));
        }
        return Result.success(p);
    }

    @PutMapping("/{id}/pay")
    public Result<?> pay(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            return Result.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态不允许付款");
        }
        order.setStatus(1);
        orderMapper.updateById(order);
        return Result.success("付款成功");
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            return Result.error(401, "请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态不允许取消");
        }
        order.setStatus(4);
        orderMapper.updateById(order);
        return Result.success("订单已取消");
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    @Data
    public static class CreateOrderRequest {
        private List<CartItem> items;
        private String addressInfo;
        private String remark;

        @Data
        public static class CartItem {
            private Long productId;
            private Integer quantity;
        }
    }
}

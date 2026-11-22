package keni.demo.orderservice.service;

import keni.demo.orderservice.model.Order;
import keni.demo.orderservice.repo.OrderRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import keni.demo.orderservice.dto.OrderDto;
import keni.demo.orderservice.dto.User;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;
    private final UserClientService userClientService;

    public OrderService(OrderRepository orderRepository, ModelMapper mapper, UserClientService userClientService) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.userClientService = userClientService;
    }

    public Order saveOrder(Order order) {
        // Validate user exists in User Service
        User user = userClientService.getUserById(order.getUserId());

        System.out.println("Fetched user: " + user.toString());
        if (user == null) {
            throw new RuntimeException("User not found with id " + order.getUserId());
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        User user = userClientService.getUserById(order.getUserId());
        
        System.err.println("Fetched user: " + user.toString());

        OrderDto dto = mapper.map(order, OrderDto.class);
        dto.setUserName(user.getName());

        return dto;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}
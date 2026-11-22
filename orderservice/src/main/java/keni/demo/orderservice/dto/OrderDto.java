package keni.demo.orderservice.dto;

import lombok.Data;

@Data
public class OrderDto {
    private String productName;
    private Double price;
    private String userName;
}

package com.baedal.pass.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    private Long storeId;
    
    private Integer categoryId; 
    private String categoryName; 

    private String storeName;
    private String address;
    private String phone;
    private String originUrl;
    private String description;
    
    private LocalTime openTime;     
    private LocalTime closeTime;
    
    private Integer minOrderPrice;
    private Integer deliveryFee;
    private String isOpen;          
    
    private Double ratingAverage;
    private Integer reviewCount;
    private LocalDateTime lastUpdated;

    private String thumbnail; 

    private List<Menu> menus; 
}
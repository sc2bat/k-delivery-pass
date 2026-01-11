package com.baedal.pass.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    private Long menuId;
    private Long storeId;
    
    private String menuNameKr;
    private String menuNameEn;
    
    private Integer price;
    private String imageUrl;
    private String description;
    
    private String isSoldOut; 
}
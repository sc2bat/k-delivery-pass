package com.baedal.pass.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    private Long addressId;
    private Long userId;
    
    private String addressName;
    private String addressMain;
    private String addressDetail;
    private String zipCode;
    
    private Double latitude;      // 위도
    private Double longitude;     // 경도
    
    private Boolean isDefault;    // 기본 배달지 여부 (DB: TinyInt/Boolean)
}
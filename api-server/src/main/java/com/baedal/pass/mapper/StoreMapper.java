package com.baedal.pass.mapper;

import com.baedal.pass.domain.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StoreMapper {

    /**
     * 가게 목록 조회
     * @param categoryId : 카테고리 ID (null이면 전체 조회)
     * @return 가게 목록 리스트
     */
    List<Store> selectStores(@Param("categoryId") Integer categoryId);

    Store selectStoreDetail(Long storeId);
}
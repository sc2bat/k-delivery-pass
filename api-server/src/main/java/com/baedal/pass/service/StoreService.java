package com.baedal.pass.service;

import com.baedal.pass.domain.Store;
import com.baedal.pass.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreMapper storeMapper;

    /**
     * 가게 목록 가져오기
     * @param categoryId (선택) 특정 카테고리만 보고 싶을 때
     */
    @Transactional(readOnly = true) // 조회 전용이므로 성능 최적화
    public List<Store> getStoreList(Integer categoryId) {
        log.info("Fetching stores for categoryId: {}", categoryId);
        return storeMapper.selectStores(categoryId);
    }

    public Store getStoreDetail(Long storeId) {
    return storeMapper.selectStoreDetail(storeId);
}
}
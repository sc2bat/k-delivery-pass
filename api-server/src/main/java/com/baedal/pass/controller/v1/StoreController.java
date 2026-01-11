package com.baedal.pass.controller.v1;

import com.baedal.pass.domain.Store;
import com.baedal.pass.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Store API", description = "가게 정보 조회 관련 API")
@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "가게 목록 조회", description = "카테고리별 가게 목록을 조회합니다. categoryId가 없으면 전체 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<Store>> getStores(
            @RequestParam(name = "categoryId", required = false) Integer categoryId) {
        List<Store> stores = storeService.getStoreList(categoryId);
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "가게 상세 조회", description = "가게 ID로 상세 정보와 메뉴 목록을 조회합니다.")
    @GetMapping("/{storeId}") // 경로 변수 사용
    public ResponseEntity<Store> getStoreDetail(@PathVariable Long storeId) {
        Store store = storeService.getStoreDetail(storeId);
        if (store == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(store);
    }
}
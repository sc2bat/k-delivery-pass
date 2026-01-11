package com.baedal.pass.service;

import com.baedal.pass.domain.Category;
import com.baedal.pass.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return categoryMapper.findAll();
    }
}
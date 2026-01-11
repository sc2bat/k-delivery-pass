package com.baedal.pass.mapper;

import com.baedal.pass.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAll();
}
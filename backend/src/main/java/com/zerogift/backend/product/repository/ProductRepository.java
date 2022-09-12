package com.zerogift.backend.product.repository;

import com.zerogift.backend.product.entity.Product;
import com.zerogift.backend.product.type.Category;
import com.zerogift.backend.product.type.Status;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findByStatusAndCategoryIn(Status status, List<Category> category, Pageable pageable);
}
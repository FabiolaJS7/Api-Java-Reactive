package com.mjscruse7.reactive.service.impl;

import com.mjscruse7.reactive.model.CategoryModel;
import com.mjscruse7.reactive.repository.CategoryRepository;
import com.mjscruse7.reactive.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    @Override
    public Flux<CategoryModel> findAll() {
        Flux<CategoryModel> categoryFlux = categoryRepository.findAll();
        return categoryFlux;
    }

    @Override
    public Mono<CategoryModel> findCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Mono<CategoryModel> save(CategoryModel category) {
        return categoryRepository.save(category);
    }
}

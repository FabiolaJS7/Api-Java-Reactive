package com.mjscruse7.reactive.service;

import com.mjscruse7.reactive.model.CategoryModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {

    Flux<CategoryModel> findAll();
    Mono<CategoryModel> findCategoryById(String id);
    Mono<CategoryModel> save(CategoryModel category);
}

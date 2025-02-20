package com.mjscruse7.reactive.repository;

import com.mjscruse7.reactive.model.CategoryModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<CategoryModel, String> {
}

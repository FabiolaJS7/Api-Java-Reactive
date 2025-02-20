package com.mjscruse7.reactive.repository;

import com.mjscruse7.reactive.model.ProductModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<ProductModel, String> {
}

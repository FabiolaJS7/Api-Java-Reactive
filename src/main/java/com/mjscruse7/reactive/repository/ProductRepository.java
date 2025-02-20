package com.mjscruse7.reactive.repository;

import com.mjscruse7.reactive.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}

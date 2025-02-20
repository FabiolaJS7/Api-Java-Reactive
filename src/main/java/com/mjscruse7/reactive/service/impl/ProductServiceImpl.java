package com.mjscruse7.reactive.service.impl;

import com.mjscruse7.reactive.model.ProductModel;
import com.mjscruse7.reactive.repository.ProductRepository;
import com.mjscruse7.reactive.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Flux<ProductModel> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Flux<ProductModel> findAllWithNameUppercase() {
        return productRepository.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        });
    }

    @Override
    public Flux<ProductModel> findAllWithNameUppercaseRepeat() {
        return findAllWithNameUppercase().repeat(5000);
    }

    @Override
    public Mono<ProductModel> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Mono<ProductModel> save(ProductModel product) {
        return productRepository.save(product);
    }

    @Override
    public Mono<Void> delete(ProductModel product) {
        return productRepository.delete(product);
    }
}

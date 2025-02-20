package com.mjscruse7.reactive.service;

import com.mjscruse7.reactive.model.ProductModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    public Flux<ProductModel> findAll();
    public Flux<ProductModel> findAllWithNameUppercase ();
    public Flux<ProductModel> findAllWithNameUppercaseRepeat ();
    public Mono<ProductModel> findById(String id);
    public Mono<ProductModel> save(ProductModel product);
    public Mono<Void> delete(ProductModel product);

}

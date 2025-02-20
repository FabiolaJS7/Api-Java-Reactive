package com.mjscruse7.reactive.service;

import com.mjscruse7.reactive.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    public Flux<Product> findAll();
    public Flux<Product> findAllWithNameUppercase ();
    public Flux<Product> findAllWithNameUppercaseRepeat ();
    public Mono<Product> findById(String id);
    public Mono<Product> save(Product product);
    public Mono<Void> delete(Product product);

}

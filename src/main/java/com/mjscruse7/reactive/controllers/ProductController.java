package com.mjscruse7.reactive.controllers;

import com.mjscruse7.reactive.model.Product;
import com.mjscruse7.reactive.repository.ProductRepository;
import com.mjscruse7.reactive.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SessionAttributes("product")
@Controller
@AllArgsConstructor
@Slf4j
public class ProductController {

    private ProductService productService;

    @GetMapping({"/listar", "/"})
    public String list(Model model) {
        Flux<Product> productFlux = productService.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                });

        productFlux.subscribe(product -> log.info("Product list: {}", product.getName()));

        model.addAttribute("productos", productFlux);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-full")
    public String listFull(Model model) {
        Flux<Product> productFlux = productService.findAllWithNameUppercaseRepeat();
        model.addAttribute("productos", productFlux);
        model.addAttribute("titulo", "Listado de productos listFull");
        return "listar";
    }

    @GetMapping("/listar-datadriver")
    public String listDataDriver(Model model) {
        Flux<Product> productFlux = productService.findAllWithNameUppercase()
                .delayElements(Duration.ofSeconds(1));

        productFlux.subscribe(product -> log.info("Product listDataDriver: {}", product.getName()));

        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productFlux, 1));
        model.addAttribute("titulo", "Listado de productos listDataDriver");

        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listChunked(Model model) {
        Flux<Product> productFlux = productService.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).repeat(5000);

        model.addAttribute("productos", productFlux);
        model.addAttribute("titulo", "Listado de productos listChunked");
        return "listar-chunked";
    }

    @GetMapping("/form")
    public Mono<String> create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("titulo", "Formulario de producto");
        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> save(Product product, SessionStatus status) {
        status.setComplete();
        return productService.save(product).doOnNext(p -> {
            log.info("Product saved: {}, Id: {}", p.getName(), p.getId());
        }).thenReturn("redirect:/listar");
    }

    @GetMapping("/form/{id}")
    public Mono<String> edit(@PathVariable ("id") String id, Model model) {
        Mono<Product> productMono = productService.findById(id).doOnNext(p -> {
            log.info("Product edited: {}, Id: {}", p.getName(), p.getId());
        }).defaultIfEmpty(new Product());

        model.addAttribute("titulo", "Editar producto");
        model.addAttribute("product", productMono);

        return Mono.just("form");

    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> editV2(@PathVariable ("id") String id, Model model) {
        return productService.findById(id).doOnNext(product -> {
                    log.info("Product edited: {}, Id: {}", product.getName(), product.getId());
                    model.addAttribute("titulo", "Editar producto");
                    model.addAttribute("product", product);
                }).defaultIfEmpty(new Product())
                .flatMap(product -> {
                    if (product.getName() == null) {
                        return Mono.error(new InterruptedException("No existe el productio"));
                    }
                    return Mono.just(product);
                })
                .then(Mono.just("form"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));

    }


}

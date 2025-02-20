package com.mjscruse7.reactive.controllers;

import com.mjscruse7.reactive.model.ProductModel;
import com.mjscruse7.reactive.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@SessionAttributes("product")
@Controller
@AllArgsConstructor
@Slf4j
public class ProductController {

    private ProductService productService;

    @GetMapping({"/listar", "/"})
    public String list(Model model) {
        Flux<ProductModel> productFlux = productService.findAll()
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
        Flux<ProductModel> productFlux = productService.findAllWithNameUppercaseRepeat();
        model.addAttribute("productos", productFlux);
        model.addAttribute("titulo", "Listado de productos listFull");
        return "listar";
    }

    @GetMapping("/listar-datadriver")
    public String listDataDriver(Model model) {
        Flux<ProductModel> productFlux = productService.findAllWithNameUppercase()
                .delayElements(Duration.ofSeconds(1));

        productFlux.subscribe(product -> log.info("Product listDataDriver: {}", product.getName()));

        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productFlux, 1));
        model.addAttribute("titulo", "Listado de productos listDataDriver");

        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listChunked(Model model) {
        Flux<ProductModel> productFlux = productService.findAll()
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
        model.addAttribute("product", new ProductModel());
        model.addAttribute("titulo", "Formulario de producto");
        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> save(@Valid ProductModel product, BindingResult result, Model model, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Errores en formulario del producto");
            model.addAttribute("botón", "Guardar");
            return Mono.just("form");
        } else {
            status.setComplete();

            if (product.getCreatedAt() == null) {
                product.setCreatedAt(new Date());
            }

            return productService.save(product).doOnNext(p -> {
                log.info("Product saved: {}, Id: {}", p.getName(), p.getId());
            }).thenReturn("redirect:/listar?success=producto+guardad+con+exito");
        }

    }

    @GetMapping("/form/{id}")
    public Mono<String> edit(@PathVariable ("id") String id, Model model) {
        Mono<ProductModel> productMono = productService.findById(id).doOnNext(p -> {
            log.info("Product edited: {}, Id: {}", p.getName(), p.getId());
        }).defaultIfEmpty(new ProductModel());

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
                }).defaultIfEmpty(new ProductModel())
                .flatMap(product -> {
                    if (product.getName() == null) {
                        return Mono.error(new InterruptedException("No existe el productio"));
                    }
                    return Mono.just(product);
                })
                .then(Mono.just("form"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));

    }

    @GetMapping("/eliminar/{id}")
    public Mono<String> delete(@PathVariable String id) {
        return productService.findById(id)
                .defaultIfEmpty(new ProductModel())
                .flatMap(product -> {
                    if (product.getId() == null) {
                        return Mono.error(new InterruptedException("No existe el producto"));
                    }
                    return Mono.just(product);
                })
                .flatMap(product -> {
                    log.info("Product eliminado: {}, Id: {}", product.getName(), product.getId());
            return productService.delete(product);
        }).then(Mono.just("redirect:/listar?success=product+eliminado+con+exito"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));

    }


}

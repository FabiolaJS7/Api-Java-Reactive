package com.mjscruse7.reactive.controllers;

import com.mjscruse7.reactive.config.PhotoConfig;
import com.mjscruse7.reactive.model.CategoryModel;
import com.mjscruse7.reactive.model.ProductModel;
import com.mjscruse7.reactive.service.CategoryService;
import com.mjscruse7.reactive.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@SessionAttributes("product")
@Controller
@AllArgsConstructor
@Slf4j
public class ProductController {

    private ProductService productService;
    private CategoryService categoryService;
    private PhotoConfig photoConfig;



    @ModelAttribute("categorias")
    public Flux<CategoryModel> categories() {
        return categoryService.findAll();
    }

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
    public Mono<String> save(@Valid ProductModel product, BindingResult result, Model model,
                             @RequestPart ("file") FilePart file, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Errores en formulario del producto");
            model.addAttribute("botón", "Guardar");
            return Mono.just("form");
        } else {
            status.setComplete();

            Mono<CategoryModel> categoryModelMono = categoryService.findCategoryById(product.getCategory().getId());

            return categoryModelMono.flatMap(categoryModel -> {
                if (product.getCreatedAt() == null) {
                    product.setCreatedAt(new Date());
                }

                if (!file.filename().isEmpty()) {
                    product.setPhoto(UUID.randomUUID().toString() + "-" + file.filename()
                            .replace(" ", "")
                            .replace(":", "")
                            .replace("\\", "")
                    );
                }

                product.setCategory(categoryModel);
                return productService.save(product);

            }).doOnNext(productModel -> {
                log.info("Category assigned: {} , Id cat: {}", productModel.getCategory().getName(), productModel.getCategory().getId());
                log.info("Product saved: {}, Id: {}", productModel.getName(), productModel.getId());
            }).flatMap(productModel -> {
                if (!file.filename().isEmpty()) {
                    return file.transferTo(new File(photoConfig.getUploadPath() + productModel.getPhoto()));
                }

                return Mono.empty();
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

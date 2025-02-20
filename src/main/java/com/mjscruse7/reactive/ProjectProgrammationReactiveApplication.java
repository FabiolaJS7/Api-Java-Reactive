package com.mjscruse7.reactive;

import com.mjscruse7.reactive.model.CategoryModel;
import com.mjscruse7.reactive.model.ProductModel;
import com.mjscruse7.reactive.service.CategoryService;
import com.mjscruse7.reactive.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
@AllArgsConstructor
public class ProjectProgrammationReactiveApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(ProjectProgrammationReactiveApplication.class);

	private ProductService productService;
	private CategoryService categoryService;

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(ProjectProgrammationReactiveApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reactiveMongoTemplate.dropCollection("products").subscribe();
		reactiveMongoTemplate.dropCollection("categories").subscribe();

		CategoryModel catElectronic = new CategoryModel("Electronico");
		CategoryModel catSport = new CategoryModel("Sport");
		CategoryModel catTechnology = new CategoryModel("Technology");
		CategoryModel catStudy = new CategoryModel("Study");


		Flux.just(catElectronic, catSport, catTechnology, catStudy)
				.flatMap(categoryService::save)
				.doOnNext(categoryModel -> {
					log.info("Save category: {}, {}", categoryModel.getName(), categoryModel.getId());
				}).thenMany(  //para incluir otro flujo del tipo Flux

						Flux.just(new ProductModel("TV Panasonic", 456.00, catElectronic),
										new ProductModel("Celular", 234.98, catTechnology),
										new ProductModel("Televisor", 13.34, catElectronic),
										new ProductModel("Balón", 564.9, catSport),
										new ProductModel("Tablet", 78.8, catTechnology))
								.flatMap(productModel -> {
									productModel.setCreatedAt(new Date());
									return productService.save(productModel);
								})

				)

				.subscribe(productMono -> log.info("Inserted product: {}", productMono.getId() + " " + productMono.getName()));
	}

}

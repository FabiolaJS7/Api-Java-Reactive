package com.mjscruse7.reactive;

import com.mjscruse7.reactive.model.ProductModel;
import com.mjscruse7.reactive.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ProjectProgrammationReactiveApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(ProjectProgrammationReactiveApplication.class);
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(ProjectProgrammationReactiveApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reactiveMongoTemplate.dropCollection("products").subscribe();

		Flux
				.just(new ProductModel("TV Panasonic", 456.00),
						new ProductModel("Celular", 234.98),
						new ProductModel("televisor", 123.34))
				.flatMap(product -> productRepository.save(product))
				.subscribe(productMono -> log.info("Inserted product: {}", productMono.getId() + " " + productMono.getName()));
	}

}

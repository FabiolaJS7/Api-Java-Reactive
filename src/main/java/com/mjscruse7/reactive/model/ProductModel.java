package com.mjscruse7.reactive.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Getter
@Setter
@Document(collection = "products")
@NoArgsConstructor
public class ProductModel {

    @Id
    private String id;
    @NotEmpty
    private String name;
    @NotNull
    private Double price;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
    @Valid
    private CategoryModel category;
    private String photo;


    public ProductModel(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public ProductModel(String name, Double price, CategoryModel category) {
        this(name,price);
        this.category = category;
    }

}

package com.mjscruse7.reactive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "categories")
public class CategoryModel {

    @Id
    private String id;
    private String name;

    public CategoryModel(String name) {
        this.name = name;
    }

    public CategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

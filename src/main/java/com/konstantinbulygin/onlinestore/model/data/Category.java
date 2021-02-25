package com.konstantinbulygin.onlinestore.model.data;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min = 3, message = "Name should be 3 characters long")
    private String name;

    @Size(min = 3, message = "Name should be 3 characters long")
    private String slug;
}

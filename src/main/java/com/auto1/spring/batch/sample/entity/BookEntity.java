package com.auto1.spring.batch.sample.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String authorFullName;

}

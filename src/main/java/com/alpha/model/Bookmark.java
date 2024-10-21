package com.alpha.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Property property;
}

package com.alpha.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "`like`") // Escaping the reserved word
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Property property;
}

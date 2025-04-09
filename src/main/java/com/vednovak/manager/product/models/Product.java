package com.vednovak.manager.product.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = Product.TABLE)
public class Product {

    static final String TABLE = "products";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal priceEur;

    @Column(nullable = false)
    private BigDecimal priceUsd;

    private Boolean isAvailable;
}

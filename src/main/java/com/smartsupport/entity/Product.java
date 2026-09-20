package com.smartsupport.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private Boolean inStock;
    private String sizes;
    @Column(length=2000) private String description;
    public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getBrand(){return brand;} public void setBrand(String v){brand=v;} public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;} public Boolean getInStock(){return inStock;} public void setInStock(Boolean v){inStock=v;}
    public String getSizes(){return sizes;} public void setSizes(String v){sizes=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
}

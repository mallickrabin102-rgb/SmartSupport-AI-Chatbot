package com.smartsupport.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="coupons")
public class Coupon {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String code;
    private BigDecimal discountPercent;
    private BigDecimal minimumOrder;
    private Boolean active;
    private String description;
    public Long getId(){return id;} public String getCode(){return code;} public void setCode(String v){code=v;} public BigDecimal getDiscountPercent(){return discountPercent;} public void setDiscountPercent(BigDecimal v){discountPercent=v;}
    public BigDecimal getMinimumOrder(){return minimumOrder;} public void setMinimumOrder(BigDecimal v){minimumOrder=v;} public Boolean getActive(){return active;} public void setActive(Boolean v){active=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
}

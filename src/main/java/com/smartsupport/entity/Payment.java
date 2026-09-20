package com.smartsupport.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="payments")
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(optional=false) private Order order;
    private BigDecimal amount;
    private String method;
    private String status;
    private Boolean amountDebited;
    private String transactionId;
    public Long getId(){return id;} public Order getOrder(){return order;} public void setOrder(Order v){order=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getMethod(){return method;} public void setMethod(String v){method=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Boolean getAmountDebited(){return amountDebited;} public void setAmountDebited(Boolean v){amountDebited=v;} public String getTransactionId(){return transactionId;} public void setTransactionId(String v){transactionId=v;}
}

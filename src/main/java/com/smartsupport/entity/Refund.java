package com.smartsupport.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="refunds")
public class Refund {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(optional=false) private Order order;
    private BigDecimal amount;
    private String status;
    private String reason;
    private LocalDate initiatedDate;
    private LocalDate completedDate;
    public Long getId(){return id;} public Order getOrder(){return order;} public void setOrder(Order v){order=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;}
    public LocalDate getInitiatedDate(){return initiatedDate;} public void setInitiatedDate(LocalDate v){initiatedDate=v;} public LocalDate getCompletedDate(){return completedDate;} public void setCompletedDate(LocalDate v){completedDate=v;}
}

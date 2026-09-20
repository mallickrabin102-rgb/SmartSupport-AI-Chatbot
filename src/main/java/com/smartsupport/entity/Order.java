package com.smartsupport.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="orders")
public class Order {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String orderNumber;
    @ManyToOne(optional=false) private Customer customer;
    private String productName;
    private String size;
    private BigDecimal amount;
    private String status;
    private String deliveryStatus;
    private String currentLocation;
    private LocalDate orderDate;
    private LocalDate expectedDelivery;
    private LocalDate deliveredDate;
    private Boolean returnEligible;
    private Boolean exchangeEligible;
    private Boolean cancelled;
    public Long getId(){return id;} public String getOrderNumber(){return orderNumber;} public void setOrderNumber(String v){orderNumber=v;}
    public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;} public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
    public String getSize(){return size;} public void setSize(String v){size=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getDeliveryStatus(){return deliveryStatus;} public void setDeliveryStatus(String v){deliveryStatus=v;}
    public String getCurrentLocation(){return currentLocation;} public void setCurrentLocation(String v){currentLocation=v;} public LocalDate getOrderDate(){return orderDate;} public void setOrderDate(LocalDate v){orderDate=v;}
    public LocalDate getExpectedDelivery(){return expectedDelivery;} public void setExpectedDelivery(LocalDate v){expectedDelivery=v;} public LocalDate getDeliveredDate(){return deliveredDate;} public void setDeliveredDate(LocalDate v){deliveredDate=v;}
    public Boolean getReturnEligible(){return returnEligible;} public void setReturnEligible(Boolean v){returnEligible=v;} public Boolean getExchangeEligible(){return exchangeEligible;} public void setExchangeEligible(Boolean v){exchangeEligible=v;}
    public Boolean getCancelled(){return cancelled;} public void setCancelled(Boolean v){cancelled=v;}
}

package com.smartsupport.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="return_requests")
public class ReturnRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(optional=false) private Order order;
    private String type;
    private String status;
    private String reason;
    private LocalDate requestedDate;
    public Long getId(){return id;} public Order getOrder(){return order;} public void setOrder(Order v){order=v;} public String getType(){return type;} public void setType(String v){type=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public LocalDate getRequestedDate(){return requestedDate;} public void setRequestedDate(LocalDate v){requestedDate=v;}
}

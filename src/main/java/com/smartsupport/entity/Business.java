package com.smartsupport.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "business")
public class Business {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String hours;

    @Column(length=2000)
    private String description;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String v) { name = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getHours() { return hours; }
    public void setHours(String v) { hours = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
}

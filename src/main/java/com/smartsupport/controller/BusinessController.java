package com.smartsupport.controller;

import com.smartsupport.entity.Business;
import com.smartsupport.service.BusinessService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@CrossOrigin
public class BusinessController {
    private final BusinessService service;

    public BusinessController(BusinessService service) { this.service = service; }

    @GetMapping public Business get() { return service.getBusiness(); }
    @PutMapping public Business update(@RequestBody Business business) {
        return service.saveBusiness(business);
    }
}

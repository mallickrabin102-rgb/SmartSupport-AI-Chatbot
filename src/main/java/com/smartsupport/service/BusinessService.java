package com.smartsupport.service;

import com.smartsupport.entity.Business;
import com.smartsupport.repository.BusinessRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {
    private final BusinessRepository repository;

    public BusinessService(BusinessRepository repository) { this.repository = repository; }

    public Business getBusiness() {
        return repository.findAll().stream().findFirst().orElse(null);
    }

    public Business saveBusiness(Business data) {
        Business existing = getBusiness();
        if (existing != null) {
            existing.setName(data.getName());
            existing.setPhone(data.getPhone());
            existing.setEmail(data.getEmail());
            existing.setHours(data.getHours());
            existing.setDescription(data.getDescription());
            return repository.save(existing);
        }
        return repository.save(data);
    }
}

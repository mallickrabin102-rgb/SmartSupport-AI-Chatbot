package com.smartsupport.controller;

import com.smartsupport.entity.Faq;
import com.smartsupport.service.FaqService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin
public class FaqController {
    private final FaqService service;

    public FaqController(FaqService service) { this.service = service; }

    @GetMapping public List<Faq> all() { return service.getAll(); }
    @PostMapping public Faq create(@RequestBody Faq faq) { return service.create(faq); }
    @PutMapping("/{id}") public Faq update(@PathVariable Long id, @RequestBody Faq faq) {
        return service.update(id, faq);
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}

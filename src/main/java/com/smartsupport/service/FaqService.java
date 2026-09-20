package com.smartsupport.service;

import com.smartsupport.entity.Faq;
import com.smartsupport.repository.FaqRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FaqService {
    private final FaqRepository repository;

    public FaqService(FaqRepository repository) { this.repository = repository; }

    public List<Faq> getAll() { return repository.findAll(); }
    public Faq create(Faq faq) { return repository.save(faq); }

    public Faq update(Long id, Faq data) {
        Faq faq = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
        faq.setQuestion(data.getQuestion());
        faq.setKeywords(data.getKeywords());
        faq.setAnswer(data.getAnswer());
        return repository.save(faq);
    }

    public void delete(Long id) { repository.deleteById(id); }
}

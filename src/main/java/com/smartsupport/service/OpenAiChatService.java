package com.smartsupport.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsupport.entity.Business;
import com.smartsupport.entity.Faq;
import com.smartsupport.repository.BusinessRepository;
import com.smartsupport.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiChatService {
    private final RestClient client;
    private final ObjectMapper mapper;
    private final FaqRepository faqRepository;
    private final BusinessRepository businessRepository;
    private final EcommerceSupportService ecommerce;

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-5.6-luna}")
    private String model;

    public OpenAiChatService(RestClient.Builder builder,
                             ObjectMapper mapper,
                             FaqRepository faqRepository,
                             BusinessRepository businessRepository, EcommerceSupportService ecommerce) {
        this.client = builder.baseUrl("https://api.openai.com/v1").build();
        this.mapper = mapper;
        this.faqRepository = faqRepository;
        this.businessRepository = businessRepository;
        this.ecommerce = ecommerce;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("YOUR_");
    }

    public String answer(String userMessage) {
        if (!isConfigured()) return null;

        String context = buildBusinessContext() + "\n" + ecommerce.buildContext(userMessage);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("instructions",
                "You are SmartSupport, a professional business customer-support AI assistant. " +
                "Answer the customer's question naturally and directly. " +
                "Use the business information and FAQ context below when relevant. " +
                "Never invent prices, policies, contact details, opening hours, order status, refunds, " +
                "payment status, delivery status, return eligibility, coupon status, or other business facts that are not present in the context. " +
                "If the context does not contain the required business-specific fact, say that you do not have " +
                "that information and suggest contacting support. " +
                "You can still answer general conversational questions naturally. " +
                "Be concise, friendly, useful, and professional. " +
                "Do not mention that you are using an API, prompt, database, or knowledge base. " +
                "Do not use markdown tables. " +
                "Customer language may be English or Hinglish; reply in the language/style used by the customer.");
        body.put("input", context + "\n\nCustomer message:\n" + userMessage);
        body.put("max_output_tokens", 300);

        try {
            String raw = client.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .body(body)
                    .retrieve()
                    .body(String.class);

            if (raw == null || raw.isBlank()) return null;
            JsonNode root = mapper.readTree(raw);
            String text = extractText(root);
            return text == null || text.isBlank() ? null : text.trim();
        } catch (Exception ex) {
            return null;
        }
    }

    private String buildBusinessContext() {
        StringBuilder context = new StringBuilder();
        context.append("Business context:\n");

        Business business = businessRepository.findAll().stream().findFirst().orElse(null);
        if (business != null) {
            context.append("Business name: ").append(value(business.getName())).append('\n');
            context.append("Phone: ").append(value(business.getPhone())).append('\n');
            context.append("Email: ").append(value(business.getEmail())).append('\n');
            context.append("Hours: ").append(value(business.getHours())).append('\n');
            context.append("Description: ").append(value(business.getDescription())).append('\n');
        }

        context.append("\nRelevant FAQ context:\n");
        List<Faq> all = faqRepository.findAll();
        if (all.isEmpty()) {
            context.append("No FAQ entries are available.\n");
        } else {
            for (Faq faq : all) {
                context.append("- Q: ").append(value(faq.getQuestion()))
                        .append(" | A: ").append(value(faq.getAnswer()))
                        .append('\n');
            }
        }
        return context.toString();
    }

    private String extractText(JsonNode root) {
        StringBuilder result = new StringBuilder();
        JsonNode output = root.path("output");
        if (output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.path("content");
                if (content.isArray()) {
                    for (JsonNode part : content) {
                        JsonNode text = part.get("text");
                        if (text != null && text.isTextual()) {
                            if (!result.isEmpty()) result.append('\n');
                            result.append(text.asText());
                        }
                    }
                }
            }
        }
        if (!result.isEmpty()) return result.toString();
        JsonNode outputText = root.get("output_text");
        return outputText != null && outputText.isTextual() ? outputText.asText() : null;
    }

    private String value(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }
}

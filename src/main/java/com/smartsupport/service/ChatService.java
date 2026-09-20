package com.smartsupport.service;

import com.smartsupport.dto.ChatResponse;
import com.smartsupport.entity.Business;
import com.smartsupport.entity.Chat;
import com.smartsupport.entity.Faq;
import com.smartsupport.repository.BusinessRepository;
import com.smartsupport.repository.ChatRepository;
import com.smartsupport.repository.FaqRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepository chats;
    private final FaqRepository faqs;
    private final BusinessRepository businesses;
    private final OpenAiChatService ai;
    private final EcommerceSupportService ecommerce;

    public ChatService(ChatRepository chats, FaqRepository faqs,
                       BusinessRepository businesses, OpenAiChatService ai, EcommerceSupportService ecommerce) {
        this.chats = chats;
        this.faqs = faqs;
        this.businesses = businesses;
        this.ai = ai;
        this.ecommerce = ecommerce;
    }

    public ChatResponse processMessage(String message) {
        String original = message == null ? "" : message.trim();

        if (original.isBlank()) {
            return reply(original, "Please type a message so I can help you.", "UNANSWERED");
        }

        // Transactional e-commerce questions are verified against MySQL first.
        // This prevents the LLM from inventing order/payment/refund facts.
        String verifiedAnswer = ecommerce.deterministic(original);
        if (verifiedAnswer != null && !verifiedAnswer.isBlank()) {
            return reply(original, verifiedAnswer, "VERIFIED");
        }

        // For general shopping/help questions, the LLM can answer using the verified
        // business + FAQ + e-commerce context supplied by OpenAiChatService.
        String aiAnswer = ai.answer(original);
        if (aiAnswer != null && !aiAnswer.isBlank()) {
            return reply(original, aiAnswer, "ANSWERED");
        }

        return reply(original, localFallback(original), "UNANSWERED");
    }

    private String localFallback(String original) {
        String input = normalize(original);
        Business business = businesses.findAll().stream().findFirst().orElse(null);

        if (containsAny(input, "hi", "hello", "hey", "hii", "hiii", "namaste",
                "good morning", "good afternoon", "good evening")) {
            String name = business != null && business.getName() != null ? business.getName() : "our business";
            return "Hi! 👋 Welcome to " + name + ". How can I help you today?";
        }

        if (containsAny(input, "thank you", "thanks", "thank u", "thx")) {
            return "You're very welcome! 😊 If you need anything else, just ask.";
        }

        if (containsAny(input, "bye", "goodbye", "see you")) {
            return "Thanks for contacting us! 👋 Have a great day.";
        }

        Faq bestFaq = null;
        int bestScore = 0;

        for (Faq faq : faqs.findAll()) {
            Set<String> keywords = tokenize(faq.getKeywords());
            Set<String> questionWords = tokenize(faq.getQuestion());
            int score = 0;

            for (String word : keywords) if (input.contains(word)) score += 3;
            for (String word : questionWords) if (input.contains(word)) score += 1;

            String question = normalize(faq.getQuestion());
            if (input.equals(question)) score += 12;
            else if (input.contains(question) || question.contains(input)) score += 7;

            if (score > bestScore) {
                bestScore = score;
                bestFaq = faq;
            }
        }

        if (bestFaq != null && bestScore >= 3) {
            return personalize(bestFaq.getAnswer(), business);
        }

        if (ai.isConfigured()) {
            return "I couldn't reach the AI support service right now. Please try again in a moment.";
        }

        return "I'm ready to answer customer questions, but the AI API key is not configured yet. "
                + "Add your OpenAI API key in application.properties and restart the server.";
    }

    private ChatResponse reply(String message, String response, String status) {
        save(message, response, status);
        return new ChatResponse(response, status);
    }

    private String personalize(String answer, Business business) {
        if (answer == null || business == null) return answer;
        return answer.replace("{phone}", Objects.toString(business.getPhone(), "our support number"))
                .replace("{email}", Objects.toString(business.getEmail(), "our support email"))
                .replace("{hours}", Objects.toString(business.getHours(), "our regular business hours"))
                .replace("{name}", Objects.toString(business.getName(), "our business"));
    }

    private Set<String> tokenize(String text) {
        if (text == null) return Collections.emptySet();
        return Arrays.stream(normalize(text).split("\\s+"))
                .map(w -> w.replaceAll("[^a-z0-9]", ""))
                .filter(w -> w.length() >= 3)
                .collect(Collectors.toSet());
    }

    private boolean containsAny(String input, String... phrases) {
        for (String phrase : phrases) {
            if (input.equals(phrase) || input.contains(phrase)) return true;
        }
        return false;
    }

    private String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private void save(String message, String response, String status) {
        Chat chat = new Chat();
        chat.setUserMessage(message);
        chat.setBotResponse(response);
        chat.setStatus(status);
        chats.save(chat);
    }
}

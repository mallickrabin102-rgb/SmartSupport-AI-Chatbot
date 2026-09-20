package com.smartsupport.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "faqs")
public class Faq {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String question;

    @Column(nullable=false, length=1000)
    private String keywords;

    @Column(nullable=false, length=2000)
    private String answer;

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public void setQuestion(String v) { question = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { keywords = v; }
    public String getAnswer() { return answer; }
    public void setAnswer(String v) { answer = v; }
}

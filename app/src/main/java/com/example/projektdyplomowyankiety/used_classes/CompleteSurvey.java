package com.example.projektdyplomowyankiety.used_classes;

import java.util.List;

public class CompleteSurvey {

    private String name;
    private List<String> answers;

    public CompleteSurvey() {
    }

    public CompleteSurvey(String name, List<String> answers) {
        this.name = name;
        this.answers = answers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}

package com.example.projektdyplomowyankiety;

public class CountAndNameSurvey {
    private int countOfQuestions;
    private String surveyName;

    public CountAndNameSurvey() {
    }

    public CountAndNameSurvey(int countOfQuestions, String surveyName) {
        this.countOfQuestions = countOfQuestions;
        this.surveyName = surveyName;
    }

    public int getCountOfQuestions() {
        return countOfQuestions;
    }

    public void setCountOfQuestions(int countOfQuestions) {
        this.countOfQuestions = countOfQuestions;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }
}

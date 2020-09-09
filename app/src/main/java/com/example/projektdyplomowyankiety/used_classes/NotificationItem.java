package com.example.projektdyplomowyankiety.used_classes;

import java.util.List;

public class NotificationItem {

    private String surveyName;
    private String surveyNotificationTime;
    private List<String> notificationDays;

    public NotificationItem(String surveyName, String surveyNotificationTime, List<String> notificationDays) {
        this.surveyName = surveyName;
        this.surveyNotificationTime = surveyNotificationTime;
        this.notificationDays = notificationDays;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyNotificationTime() {
        return surveyNotificationTime;
    }

    public void setSurveyNotificationTime(String surveyNotificationTime) {
        this.surveyNotificationTime = surveyNotificationTime;
    }

    public List<String> getNotificationDays() {
        return notificationDays;
    }

    public void setNotificationDays(List<String> notificationDays) {
        this.notificationDays = notificationDays;
    }
}

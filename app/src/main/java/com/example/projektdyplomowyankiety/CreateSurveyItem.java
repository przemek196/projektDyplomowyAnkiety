package com.example.projektdyplomowyankiety;

import android.widget.TextView;

public class CreateSurveyItem {

    private TextView tvsurvName;
    private TextView tvqusTypname;
    private TextView tvQusCount;

    public CreateSurveyItem(TextView tvsurvName, TextView tvqusTypname, TextView tvQusCount) {
        this.tvsurvName = tvsurvName;
        this.tvqusTypname = tvqusTypname;
        this.tvQusCount = tvQusCount;
    }

    public TextView getTvsurvName() {
        return tvsurvName;
    }

    public TextView getTvqusTypname() {
        return tvqusTypname;
    }

    public TextView getTvQusCount() {
        return tvQusCount;
    }
}

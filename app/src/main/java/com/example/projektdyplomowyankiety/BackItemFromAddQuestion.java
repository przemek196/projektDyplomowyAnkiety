package com.example.projektdyplomowyankiety;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BackItemFromAddQuestion implements Parcelable {

    public BackItemFromAddQuestion() {
    }

    private String nameOfQuestion;
    private String nameOfQuestionType;
    private List<String> questionList= new ArrayList<>();

    public BackItemFromAddQuestion(String nameOfQuestion, String nameOfQuestionType, List<String> questionList) {
        this.nameOfQuestion = nameOfQuestion;
        this.nameOfQuestionType = nameOfQuestionType;
       this.questionList = questionList;
    }

    protected BackItemFromAddQuestion(Parcel in) {
        nameOfQuestion = in.readString();
        nameOfQuestionType = in.readString();
        in.readStringList(questionList);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameOfQuestion);
        dest.writeString(nameOfQuestionType);
        dest.writeStringList(questionList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BackItemFromAddQuestion> CREATOR = new Creator<BackItemFromAddQuestion>() {
        @Override
        public BackItemFromAddQuestion createFromParcel(Parcel in) {
            return new BackItemFromAddQuestion(in);
        }

        @Override
        public BackItemFromAddQuestion[] newArray(int size) {
            return new BackItemFromAddQuestion[size];
        }
    };

    public String getNameOfQuestion() {
        return nameOfQuestion;
    }

    public void setNameOfQuestion(String nameOfQuestion) {
        this.nameOfQuestion = nameOfQuestion;
    }

    public String getNameOfQuestionType() {
        return nameOfQuestionType;
    }

    public void setNameOfQuestionType(String nameOfQuestionType) {
        this.nameOfQuestionType = nameOfQuestionType;
    }

    public List<String> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<String> questionList) {
        this.questionList = questionList;
    }
}

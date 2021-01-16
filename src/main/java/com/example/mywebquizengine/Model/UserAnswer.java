package com.example.mywebquizengine.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
@Entity(name = "USER_ANSWERS")
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int answerId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ElementCollection
    @CollectionTable
    private List<Integer> answer;

    private Boolean status;

    @ManyToOne
    private Quiz quiz;

    @ManyToOne
    private User user;

    private Calendar completedAt;

    UserAnswer(ArrayList<Integer> answer){
        this.answer = answer;
    }

    public UserAnswer(){}

    public Boolean getStatus() {
        return status;
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }

    public void setQuiz(Quiz id){
        this.quiz = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setAnswerId(int id) {
        this.answerId = id;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public Calendar getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Calendar data) {
        this.completedAt = data;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

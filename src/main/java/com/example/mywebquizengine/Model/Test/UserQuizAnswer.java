package com.example.mywebquizengine.Model.Test;

import com.example.mywebquizengine.Model.Test.UserTestAnswer;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
@Entity(name = "USER_QUIZ_ANSWERS")
public class UserQuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int quizAnswerId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ElementCollection
    @CollectionTable
    private List<Integer> answer;

    private Boolean status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "quiz_id")
    //@Cascade(org.hibernate.annotations.CascadeType.DELETE)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private Quiz quiz;
    //private Test test;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "user_answer_id")
    private UserTestAnswer userAnswerId;

    private Calendar completedAt;

    UserQuizAnswer(ArrayList<Integer> answer){
        this.answer = answer;
    }

    public UserQuizAnswer(){}

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


    public void setQuizAnswerId(int id) {
        this.quizAnswerId = id;
    }

    public int getQuizAnswerId() {
        return quizAnswerId;
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

    public void setUserAnswerId(UserTestAnswer userTestAnswer) {
        this.userAnswerId = userTestAnswer;
    }

    public UserTestAnswer getUserAnswerId() {
        return userAnswerId;
    }
}

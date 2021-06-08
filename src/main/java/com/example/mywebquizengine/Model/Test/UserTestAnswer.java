package com.example.mywebquizengine.Model.Test;

import com.example.mywebquizengine.Model.User;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity(name = "USER_TEST_ANSWERS")
public class UserTestAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int userAnswerId;

    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    @OneToMany(mappedBy = "userAnswerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserQuizAnswer> userQuizAnswers;

    @ManyToOne
    @JoinColumn(nullable = false, name = "test_id")
    private Test test;

    private Calendar completedAt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserAnswerId() {
        return userAnswerId;
    }

    public void setUserAnswerId(int userAnswerId) {
        this.userAnswerId = userAnswerId;
    }

    public List<UserQuizAnswer> getUserQuizAnswers() {
        return userQuizAnswers;
    }

    public void setUserQuizAnswers(List<UserQuizAnswer> userQuizAnswers) {
        this.userQuizAnswers = userQuizAnswers;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

    public Calendar getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Calendar data) {
        this.completedAt = data;
    }


}

package com.example.mywebquizengine.Model;

import java.util.List;

public class Quizzes {

    private Long id;
    private List<Quiz> quizzes;
    private User user;

    public Quizzes() {}

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

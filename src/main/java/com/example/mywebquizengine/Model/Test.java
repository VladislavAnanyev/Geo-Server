package com.example.mywebquizengine.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.List;

@Entity(name = "TESTS")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<@Valid Quiz> quizzes;

    @ManyToOne
    @JoinColumn
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTestAnswer> answers;

    public Test() {}

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAnswers(List<UserTestAnswer> answers) {
        this.answers = answers;
    }

    public List<UserTestAnswer> getAnswers() {
        return answers;
    }
}

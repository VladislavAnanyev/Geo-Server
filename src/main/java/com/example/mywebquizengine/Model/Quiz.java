package com.example.mywebquizengine.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "QUIZZES")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NotBlank
    @NotNull
    private String title;

    @NotBlank
    @NotNull
    private String text;

    @ElementCollection
    @CollectionTable
    @NotNull
    @NotEmpty
    @Size(min = 2)
    private List<String> options;

    @ElementCollection
    @CollectionTable
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Integer> answer;

    @ManyToOne
    @JoinColumn
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    public Quiz() {}

    Quiz(String title, String text, ArrayList<String> options, List<Integer> answer) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }

    public void setUser(User author) {
        this.user = author;
    }

    public User getUser() {
        return user;
    }
}

package com.example.mywebquizengine.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ServerAnswer {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Quiz quiz;
    private boolean success;
    private String feedback;

    public ServerAnswer() {}

    public void checkAnswer(List<Integer> answer){
        if (answer.equals(quiz.getAnswer())) {
            this.feedback = "Congratulations, you're right!";
            this.success = true;
        } else {
            this.feedback = "Wrong answer! Please, try again.";
            this.success = false;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

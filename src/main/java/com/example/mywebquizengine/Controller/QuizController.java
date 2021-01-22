package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Quiz;
import com.example.mywebquizengine.Model.Test;
import com.example.mywebquizengine.Service.QuizService;
import com.example.mywebquizengine.Service.UserAnswerService;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserService userService;

    ArrayList<Quiz> quizzes = new ArrayList<>();

    /*@GetMapping(path = "/api/quizzes")
    public String getQuizzes(Model model, @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                 @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                 @RequestParam(defaultValue = "id") String sortBy) {
        reloadQuizzes();
        Page<Quiz> page1 = quizService.getAllQuizzes(page, pageSize, sortBy);
        model.addAttribute("test", page1.getContent());
        return "getAllQuiz";
    }

    @GetMapping(path = "/myquiz")
    public String getMyQuizzes(Model model, @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                            @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                            @RequestParam(defaultValue = "id") String sortBy) {
        //reloadQuizzes();
        String name = userService.getThisUser().getUsername();
        Page<Quiz> page1 = quizService.getMyQuiz(name, page, pageSize, sortBy);
        model.addAttribute("myquiz", page1.getContent());
        return "myquiz";
    }

    @GetMapping(path = "/api/quizzes/completed")
    public String getCompleted (Model model,
                                          @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                          @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                          @RequestParam(defaultValue = "completed_At") String sortBy) {
        reloadQuizzes();
        String name = userService.getThisUser().getUsername();
        model.addAttribute("comp", userAnswerService
                .getCompleted(name, page, pageSize, sortBy).getContent());
        return "mycomplete";
    }
*/
    @GetMapping(path = "/add")
    public String addQuiz(Model model) {
        return "addQuiz";
    }

    @PostMapping(path = "/api/quizzes", consumes={"application/json"})
    //@ResponseBody
    public String addQuiz(Model model, @RequestBody Test test) throws ResponseStatusException {
        try {
            //reloadQuizzes();
            test.getQuizzes().get(0).setTest(test);
            test.setUser(userService.getThisUser());
            quizService.saveTest(test);
            return "home";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/")
    public String home() {
        return "home";
    }

    /*
    @PostMapping(path = "/api/quizzes/{id}/solve")
    @ResponseBody
    public String getAnswer(Model model, @PathVariable String id, @RequestBody UserAnswer userAnswer) {
        reloadQuizzes();

        ServerAnswer thisServerAnswer = new ServerAnswer();
        thisServerAnswer.quiz = quizService.findQuiz(Integer.parseInt(id));

        userAnswer.setUser(userService.getThisUser());
        userAnswer.setQuiz(thisServerAnswer.quiz);

        thisServerAnswer.checkAnswer(userAnswer.getAnswer());

        userAnswer.setStatus(thisServerAnswer.isSuccess());
        userAnswer.setCompletedAt(new GregorianCalendar());
        userAnswer.setQuiz(thisServerAnswer.quiz);
        userAnswerService.saveAnswer(userAnswer);

        model.addAttribute("answer", thisServerAnswer);

        return userAnswer.getStatus().toString();
    }

    @GetMapping(path = "/api/quizzes/{id}/solve")
    public String getAnswer(Model model, @PathVariable String id) {
        model.addAttribute(id);
        model.addAttribute("quiz", quizService.findQuiz(Integer.parseInt(id)));
        return "answer";
    }

    public void reloadQuizzes() {
        quizzes = quizService.reloadQuiz();
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public Quiz getQuizViaId(@PathVariable Integer id) {
        reloadQuizzes();
        return quizService.findQuiz(id);
    }
*/
    @GetMapping("/reg")
    public String login(Map<String, Object> model) {
        return "reg";
    }
/*
    @DeleteMapping(path = "/api/quizzes/{id}")
    public void deleteQuiz(@PathVariable Integer id) {
        reloadQuizzes();
        quizService.findQuiz(id);
        if (userService.getThisUser().getUsername()
                .equals(quizService.findQuiz(id)
                        .getUser().getUsername())) {
            userAnswerService.deleteAnswer(id);
            quizService.deleteQuiz(id);
            reloadQuizzes();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        reloadQuizzes();
    }


    @PutMapping(path = "/update/{id}", consumes={"application/json"})
    @ResponseBody
    public void changeQuiz(Model model, @PathVariable Integer id, @RequestBody Quiz quiz) {
        if (userService.getThisUser().getUsername()
                .equals(quizService.findQuiz(id)
                        .getUser().getUsername())) {
            reloadQuizzes();
            quizService.updateQuiz(id, quiz.getTitle(), quiz.getText(), (ArrayList<String>) quiz.getOptions(), (ArrayList<Integer>) quiz.getAnswer());
            reloadQuizzes();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping(path = "/update/{id}")
    public String update(@PathVariable Integer id,   Model model) {
        if (userService.getThisUser().getUsername()
                .equals(quizService.findQuiz(id)
                        .getUser().getUsername())) {
            Quiz tempQuiz = quizService.findQuiz(id);
            model.addAttribute("oldQuiz", tempQuiz);
            return "updateQuiz";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/api/quizzes/{id}/info/")
    public String getInfo(@PathVariable Integer id, Model model,
                          @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                          @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                          @RequestParam(defaultValue = "completed_At") String sortBy) {
        model.addAttribute("stat", userAnswerService.getStatistics(id));

        model.addAttribute("answersOnQuiz", userAnswerService.getAnswersById(id, page, pageSize, sortBy).getContent());
        return "info";
    }*/
}

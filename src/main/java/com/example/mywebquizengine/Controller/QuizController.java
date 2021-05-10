package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Test.Quiz;
import com.example.mywebquizengine.Model.Test.ServerAnswer;
import com.example.mywebquizengine.Model.Test.Test;
import com.example.mywebquizengine.Model.Test.UserQuizAnswer;
import com.example.mywebquizengine.Model.Test.UserTestAnswer;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.QuizService;
import com.example.mywebquizengine.Service.TestService;
import com.example.mywebquizengine.Service.UserAnswerService;
import com.example.mywebquizengine.Service.UserService;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@Validated
@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;


    @GetMapping(path = "/api/quizzes")
    public String getQuizzes(Model model, @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                 @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                 @RequestParam(defaultValue = "id") String sortBy) {

        Page<Test> page1 = testService.getAllQuizzes(page, pageSize, sortBy);
        //model.addAttribute("quiz", page1.getContent());
        model.addAttribute("test", page1.getContent());
        return "getAllQuiz";
    }

    @GetMapping(path = "/myquiz")
    public String getMyQuizzes(Model model, @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                            @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                            @RequestParam(defaultValue = "id") String sortBy) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //String name = userService.getThisUser().getUsername();
        Page<Test> page1 = testService.getMyQuiz(user.getUsername(), page, pageSize, sortBy);
        model.addAttribute("myquiz", page1.getContent());
        //model.addAttribute("opti", page1.getContent().get(0).getQuizzes().get(0).getOptions());
        return "myquiz";
    }



    /*@GetMapping(path = "/api/quizzes/completed")
    public String getCompleted (Model model,
                                          @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                          @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                          @RequestParam(defaultValue = "completed_At") String sortBy) {
        //reloadQuizzes();
        String name = userService.getThisUser().getUsername();
        model.addAttribute("comp", userAnswerService
                .getCompleted(name, page, pageSize, sortBy).getContent());
        return "mycomplete";
    }*/

    @GetMapping(path = "/add")
    public String addQuiz(Model model) {
        return "addQuiz";
    }


    @PostMapping(path = "/api/quizzes", consumes={"application/json"})
    public String addQuiz(Model model, @RequestBody @Valid Test test) throws ResponseStatusException {
        try {

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user.setTests(new ArrayList<>());
            user.setRoles(new ArrayList<>());
            test.setUser(user);
            for (int i = 0; i < test.getQuizzes().size(); i++) {
                test.getQuizzes().get(i).setTest(test);
            }
            testService.saveTest(test);
            quizService.saveQuiz(test.getQuizzes());
            return "home";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/")
    public String home() {
        return "home";
    }


    /*@PostMapping(path = "/api/quizzes/{id}/solve")
    @ResponseBody
    public String getAnswer(Model model, @PathVariable String id, @RequestBody QuizAnswer quizAnswer) {
        //reloadQuizzes();

        ServerAnswer thisServerAnswer = new ServerAnswer();
        thisServerAnswer.quiz = quizService.findQuiz(Integer.parseInt(id));

        quizAnswer.setUser(userService.getThisUser());
        quizAnswer.setQuiz(thisServerAnswer.quiz);

        thisServerAnswer.checkAnswer(quizAnswer.getAnswer());

        quizAnswer.setStatus(thisServerAnswer.isSuccess());
        quizAnswer.setCompletedAt(new GregorianCalendar());
        quizAnswer.setQuiz(thisServerAnswer.quiz);
        userAnswerService.saveAnswer(quizAnswer);

        model.addAttribute("answer", thisServerAnswer);

        return quizAnswer.getStatus().toString();
    }*/


    @PostMapping(path = "/api/quizzes/{id}/solve")
    @ResponseBody
    public String getAnswerOnTest(Model model, @PathVariable String id, @RequestBody UserTestAnswer userTestAnswer) {

        StringBuilder result = new StringBuilder();
        List<UserQuizAnswer> userQuizAnswers = new ArrayList<>();
        userTestAnswer.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        userTestAnswer.setTest(testService.findTest(Integer.parseInt(id)));
        //for (int i = 0; i < userTestAnswer.getUserQuizAnswers().size(); i++) {

        for (int i = 0; i < testService.findTest(Integer.parseInt(id)).getQuizzes().size(); i++) {
            UserQuizAnswer quizAnswer = new UserQuizAnswer();
            ServerAnswer thisServerAnswer = new ServerAnswer();
            thisServerAnswer.quiz = quizService.findQuiz(testService.findTest(Integer.parseInt(id)).getQuizzes().get(i).getId());

            //quizAnswer.setUser(userService.getThisUser());
            quizAnswer.setQuiz(thisServerAnswer.quiz);

            thisServerAnswer.checkAnswer(userTestAnswer.getUserQuizAnswers().get(i).getAnswer());

            quizAnswer.setStatus(thisServerAnswer.isSuccess());
            quizAnswer.setCompletedAt(new GregorianCalendar());
            quizAnswer.setQuiz(thisServerAnswer.quiz);

            quizAnswer.setUserAnswerId(userTestAnswer);

            quizAnswer.setAnswer(userTestAnswer.getUserQuizAnswers().get(i).getAnswer());

            //userAnswerService.saveAnswer(quizAnswer);


            userQuizAnswers.add(quizAnswer);

            model.addAttribute("answer", thisServerAnswer);

            if (quizAnswer.getStatus().toString().equals("true")) {
                result.append("1");
            } else {
                result.append("0");
            }
            //results.add(quizAnswer.getStatus().toString());
        }


        userTestAnswer.setUserQuizAnswers(userQuizAnswers);

        userAnswerService.saveAnswer(userTestAnswer);

        return String.valueOf(result);
    }




    @GetMapping(path = "/api/quizzes/{id}/solve")
    public String getAnswer(Model model, @PathVariable String id) {
        model.addAttribute(id);
        //model.addAttribute("quiz", quizService.findQuiz(Integer.parseInt(id)));
        model.addAttribute("test_id", testService.findTest(Integer.parseInt(id)));
        model.addAttribute("test", testService.findTest(Integer.parseInt(id)).getQuizzes());
        return "answer";
    }

    @GetMapping("/reg")
    public String login(Map<String, Object> model) {
        return "reg";
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    public void deleteTest(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUsername()
                .equals(testService.findTest(id)
                        .getUser().getUsername())) {
            //userAnswerService.deleteAnswer(id);
            testService.deleteTest(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }


    @PutMapping(path = "/update/{id}", consumes={"application/json"})
    @ResponseBody
    public void changeTest(Model model, @PathVariable Integer id, @Valid @RequestBody Test test) throws ResponseStatusException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUsername()
                .equals(testService.findTest(id)
                        .getUser().getUsername())) {

            for (int i = 0; i < test.getQuizzes().size(); i++) {
                Quiz oldQuiz = testService.findTest(id).getQuizzes().get(i);
                Quiz quiz = test.getQuizzes().get(i);
                quizService.updateQuiz(oldQuiz.getId(), quiz.getTitle(), quiz.getText(), (ArrayList<String>) quiz.getOptions(), (ArrayList<Integer>) quiz.getAnswer());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

    }


    @GetMapping(path = "/update/{id}")
    public String update(@PathVariable Integer id,   Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUsername()
                .equals(testService.findTest(id)
                        .getUser().getUsername())) {
            Test tempTest = testService.findTest(id);
            model.addAttribute("oldTest", tempTest);
            return "updateQuiz";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/api/quizzes/{id}/info/")
    public String getInfo(@PathVariable Integer id, Model model,
                          @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                          @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                          @RequestParam(defaultValue = "user_username") String sortBy) {

        ArrayList<Integer> answers = userAnswerService.getAnswersByTestId(id);
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            result.add(userAnswerService.getStatistics(id, answers.get(i)));
        }
        model.addAttribute("stat", result.toArray());
        model.addAttribute("answersOnQuiz", userAnswerService.getAnswersById(id, page, pageSize, sortBy).getContent());
        return "info";
    }
}

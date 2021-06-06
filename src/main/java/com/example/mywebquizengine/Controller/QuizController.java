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
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@Validated
@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    public UserService userService;

    @Autowired
    private TestService testService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;


    @GetMapping(path = "/api/quizzes")
    public String getQuizzes(Model model, @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                 @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                 @RequestParam(defaultValue = "id") String sortBy) throws IOException {

        Page<Test> page1 = testService.getAllQuizzes(page, pageSize, sortBy);

        //File dir = new File(System.getProperty("user.dir") + "/img/look.com.ua_2016.02-111-1920x1080"); //path указывает на директорию
        //File[] arrFiles = dir.listFiles();
        //List<File> files = Arrays.asList(arrFiles);

        //Random random = new Random(System.currentTimeMillis());

        //model.addAttribute("img", files);

        //model.addAttribute("quiz", page1.getContent());
        model.addAttribute("test", page1.getContent());
        return "getAllQuiz";
    }

    @GetMapping(path = "/myquiz")
    public String getMyQuizzes(Authentication authentication, Model model, @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                            @RequestParam(required = false,defaultValue = "10") @Min(1) @Max(10) Integer pageSize,
                                            @RequestParam(defaultValue = "id") String sortBy) {
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = getAuthUser(authentication, userService);
        Page<Test> page1 = testService.getMyQuiz(user.getUsername(), page, pageSize, sortBy);
        model.addAttribute("myquiz", page1.getContent());

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
    public String addQuiz(Model model, @RequestBody @Valid Test test, Authentication authentication) throws ResponseStatusException {
        try {

            //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User user = getAuthUser(authentication, userService);

            user.setTests(new ArrayList<>()); // Handle Persistance bug
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
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        return "home";
    }


    @PostMapping(path = "/api/quizzes/{id}/solve")
    @ResponseBody
    public String getAnswerOnTest(Authentication authentication, Model model, @PathVariable String id, @RequestBody UserTestAnswer userTestAnswer) {

        StringBuilder result = new StringBuilder();
        List<UserQuizAnswer> userQuizAnswers = new ArrayList<>();


        User user = getAuthUser(authentication, userService);

        userTestAnswer.setUser(user);
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
            //quizAnswer.setCompletedAt(new GregorianCalendar());
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

        userTestAnswer.setCompletedAt(new GregorianCalendar());

        userAnswerService.saveAnswer(userTestAnswer);



        return String.valueOf(result);
    }


    /*@GetMapping(path = "/api/quizzes/{id}/solve/info")
    @ResponseBody
    public int getCurrentAnswer(Model model, @PathVariable String id) {
        //model.addAttribute(id);
        //model.addAttribute("quiz", quizService.findQuiz(Integer.parseInt(id)));
        return userAnswerService.getStat(Integer.parseInt(id));
    }*/


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
    public void deleteTest(@PathVariable Integer id, Authentication authentication) {

        User user = getAuthUser(authentication, userService);


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
    public void changeTest(Model model, @PathVariable Integer id, @Valid @RequestBody Test test,
                           Authentication authentication) throws ResponseStatusException {
        User user = getAuthUser(authentication, userService);
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
    public String update(@PathVariable Integer id, Model model, Authentication authentication) {
        User user = getAuthUser(authentication, userService);

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

    public static User getAuthUser(Authentication authentication, UserService userService) {
        String name = "";



        if (authentication instanceof OAuth2AuthenticationToken) {

            if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("google")) {

                name = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes().get("email")
                        .toString().replace("@gmail.com", "");
            } else if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("github")) {
                name = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes().get("name")
                        .toString();
            }

        } else {
            User user = (User) authentication.getPrincipal();
            name = user.getUsername();
        }

        return userService.reloadUser(name).get();
    }

    @GetMapping(path = "/api/quizzes/{id}/info/")
    public String getInfo(@PathVariable Integer id, Model model,
                          @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                          @RequestParam(required = false,defaultValue = "2000") @Min(1) @Max(2000) Integer pageSize,
                          @RequestParam(defaultValue = "completed_at") String sortBy) {

        ArrayList<Integer> answers = userAnswerService.getAnswersByTestId(id);
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            result.add(userAnswerService.getStatistics(id, answers.get(i)));
        }

        result.sort(Collections.reverseOrder());

        model.addAttribute("stat", result.toArray());
        model.addAttribute("answersOnQuiz", userAnswerService.getAnswersById(id, page, pageSize, sortBy).getContent());
        return "info";
    }
}

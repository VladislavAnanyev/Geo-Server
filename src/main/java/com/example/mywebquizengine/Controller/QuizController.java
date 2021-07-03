package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Test.*;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.QuizService;
import com.example.mywebquizengine.Service.TestService;
import com.example.mywebquizengine.Service.UserAnswerService;
import com.example.mywebquizengine.Service.UserService;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.*;

import static com.example.mywebquizengine.Controller.UserController.getAuthUser;

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

    // Проверка наличия неотправленных ответов
    @GetMapping(path = "/checklastanswer/{id}")
    @ResponseBody
    public Boolean checkLastAnswer(@PathVariable String id, Authentication authentication) {
        if (userAnswerService.checkLastComplete(getAuthUser(authentication, userService), id) != null) {
            return userAnswerService.checkLastComplete(getAuthUser(authentication, userService), id).getCompletedAt() == null;
        } else {
            return false;
        }
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
            test.setDuration(test.getDuration());
            test.setUser(user);
            for (int i = 0; i < test.getQuizzes().size(); i++) {
                test.getQuizzes().get(i).setTest(test);
            }
            testService.saveTest(test);
            //quizService.saveQuiz(test.getQuizzes());
            return "redirect:/";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/")
    public String home() {
        //Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        return "home";
    }


    @PostMapping(path = "/api/quizzes/{id}/solve")
    @ResponseBody
    public String getAnswerOnTest(Authentication authentication, @PathVariable String id,
                                  @RequestBody UserTestAnswer userTestAnswer) {

        StringBuilder result = new StringBuilder();
        List<UserQuizAnswer> userQuizAnswers = new ArrayList<>();
        int trueAnswers = 0;
        User user = getAuthUser(authentication, userService);

        userTestAnswer.setUser(user);
        userTestAnswer.setTest(testService.findTest(Integer.parseInt(id)));


        for (int i = 0; i < testService.findTest(Integer.parseInt(id)).getQuizzes().size(); i++) {
            UserQuizAnswer quizAnswer = new UserQuizAnswer();
            AnswerChecker answerChecker = new AnswerChecker();

            answerChecker.quiz = quizService.findQuiz(testService.findTest(Integer.parseInt(id)).getQuizzes().get(i).getId());
            quizAnswer.setQuiz(answerChecker.quiz);

            answerChecker.checkAnswer(userTestAnswer.getUserQuizAnswers().get(i).getAnswer());
            quizAnswer.setStatus(answerChecker.isSuccess());

            quizAnswer.setAnswer(userTestAnswer.getUserQuizAnswers().get(i).getAnswer());

            userQuizAnswers.add(quizAnswer);

            if (quizAnswer.getStatus().toString().equals("true")) {
                result.append("1");
                trueAnswers++;
            } else {
                result.append("0");
            }
        }

        userTestAnswer.setUserQuizAnswers(userQuizAnswers);

        userTestAnswer.setPercent(((double) trueAnswers/(double)result.length()) * 100.0);

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
        Calendar nowDate = new GregorianCalendar();
        nowDate.setTimeZone(timeZone);

        userTestAnswer.setCompletedAt(nowDate);

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
    public String passTest(Model model, @PathVariable String id,
                           @RequestParam(required = false, defaultValue = "false") String restore,
                           Authentication authentication) {

        UserTestAnswer lastUserTestAnswer = userAnswerService.checkLastComplete(UserController.getAuthUser(authentication, userService), id);

        if (Boolean.parseBoolean(restore) && lastUserTestAnswer != null && lastUserTestAnswer.getCompletedAt() == null) {

            model.addAttribute("lastAnswer", lastUserTestAnswer);

        } else {

            UserTestAnswer userTestAnswer = new UserTestAnswer();

            userTestAnswer.setStartAt(new GregorianCalendar());
            userTestAnswer.setTest(testService.findTest(Integer.parseInt(id)));
            userTestAnswer.setUser(UserController.getAuthUser(authentication, userService));
            userAnswerService.saveStartAnswer(userTestAnswer);

        }

        model.addAttribute("test_id", testService.findTest(Integer.parseInt(id)));
        return "answer";
    }

    @GetMapping("/reg")
    public String login(Map<String, Object> model) {
        return "reg";
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    @PreAuthorize(value = "@testService.findTest(#id).user.username.equals(@userController.getAuthUser(authentication,@userService).username)")
    public void deleteTest(@PathVariable Integer id, Authentication authentication) {
        testService.deleteTest(id);
    }


    @PutMapping(path = "/update/{id}", consumes={"application/json"})
    @ResponseBody
    @PreAuthorize(value = "@testService.findTest(#id).user.username.equals(@userController.getAuthUser(authentication,@userService).username)")
    public void changeTest(@PathVariable Integer id, @Valid @RequestBody Test test,
                           Authentication authentication) throws ResponseStatusException {

        /*for (int i = 0; i < test.getQuizzes().size(); i++) {
            Quiz oldQuiz = testService.findTest(id).getQuizzes().get(i);
            Quiz quiz = test.getQuizzes().get(i);
            quizService.updateQuiz(oldQuiz.getId(), quiz.getTitle(), quiz.getText(), (ArrayList<String>) quiz.getOptions(), (ArrayList<Integer>) quiz.getAnswer());
        }*/

        testService.updateTest(id, test);

    }



    @GetMapping(path = "/update/{id}")
    @PreAuthorize(value = "@testService.findTest(#id).user.username.equals(@userController.getAuthUser(authentication,@userService).username)")
    public String update(@PathVariable Integer id, Model model, Authentication authentication) {

        Test tempTest = testService.findTest(id);
        model.addAttribute("oldTest", tempTest);
        return "updateQuiz";

    }



    @GetMapping(path = "/api/quizzes/{id}/info/")
    @PreAuthorize(value = "@testService.findTest(#id).user.username.equals(@userController.getAuthUser(authentication,@userService).username)")
    public String getInfo(@PathVariable Integer id, Model model,
                          @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                          @RequestParam(required = false,defaultValue = "2000") @Min(1) @Max(2000) Integer pageSize,
                          @RequestParam(defaultValue = "completed_at") String sortBy) {

        model.addAttribute("answersOnQuiz", userAnswerService.getAnswersById(id, page, pageSize, sortBy).getContent());
        return "info";
    }

    @PostMapping(value = "/answersession/{id}")
    public void getAnswerSession(Authentication authentication,@RequestBody UserTestAnswer userTestAnswer, @PathVariable String id) {
        User user = getAuthUser(authentication, userService);


        userTestAnswer.setUser(user);
        userTestAnswer.setTest(testService.findTest(Integer.parseInt(id)));
        userAnswerService.saveAnswer_2(userTestAnswer);
        throw new ResponseStatusException(HttpStatus.OK);
    }
}

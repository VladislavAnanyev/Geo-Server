package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.Test.UserTestAnswer;
import com.example.mywebquizengine.Repos.UserQuizAnswerRepository;
import com.example.mywebquizengine.Repos.UserTestAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserAnswerService  {

    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;

    @Autowired
    private UserTestAnswerRepository userTestAnswerRepository;

    /*public void saveAnswer(UserQuizAnswer userQuizAnswer){
        userQuizAnswerRepository.save(userQuizAnswer);
    }*/

    public void saveAnswer(UserTestAnswer userTestAnswer) {
        UserTestAnswer lastUserAnswer = userTestAnswerRepository.findLastUserAnswer(userTestAnswer.getUser().getUsername());
        lastUserAnswer.setCompletedAt(userTestAnswer.getCompletedAt());
        lastUserAnswer.setPercent(userTestAnswer.getPercent());

        /*
         Используется для того чтобы установить "Пользовательскому ответу на вопрос"
         экземпляр(ID) ответа, который создаётся при входе на страницу для прохождения
         теста. Это необходимо для того чтобы новая запись о прохождении теста в БД не
         создавалась, а обновлялась старая (созданная при заходе на страницу для прохождения теста)
         */
        for (int i = 0; i < userTestAnswer.getUserQuizAnswers().size(); i++) {
            userTestAnswer.getUserQuizAnswers().get(i).setUserAnswer(lastUserAnswer);
        }

        lastUserAnswer.setUserQuizAnswers(userTestAnswer.getUserQuizAnswers());
        //userTestAnswerRepository.save(lastUserAnswer);
    }

    /*public Page<UserQuizAnswer> getCompleted (String name, Integer page,
                                              Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        return userQuizAnswerRepository.getCompleteAnswersForUser(name, paging);
    }*/

    public Page<UserTestAnswer> getAnswersById (int id, Integer page, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        return userTestAnswerRepository.getAnswersOnMyQuiz(id, paging);
    }

    public Double getStatistics(Integer id, Integer answer) {
        return ((double) userQuizAnswerRepository.getTrueAnswers(id, answer)/(double) userQuizAnswerRepository.getCountById(id, answer)) * 100;
    }

    public ArrayList<Integer> getAnswersByTestId(Integer id) {
        return (ArrayList<Integer>) userTestAnswerRepository.getUserAnswersById(id);
    }

    public void saveStartAnswer(UserTestAnswer userTestAnswer) {
        userTestAnswerRepository.save(userTestAnswer);
    }

    /*public Integer getStat(Integer id) {
        return userTestAnswerRepository.getLastFalseById();
    }*/

   /* public void deleteAnswer(Integer id) {
        List<Integer> answers = userQuizAnswerRepository.getAnswerIdForQuiz(id);
        answers.forEach(answer -> userQuizAnswerRepository.deleteById(answer));
    }*/
}

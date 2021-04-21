package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.Test.UserQuizAnswer;
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

    public void saveAnswer(UserQuizAnswer userQuizAnswer){
        userQuizAnswerRepository.save(userQuizAnswer);
    }

    public void saveAnswer(UserTestAnswer userTestAnswer){
        userTestAnswerRepository.save(userTestAnswer);
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

   /* public void deleteAnswer(Integer id) {
        List<Integer> answers = userQuizAnswerRepository.getAnswerIdForQuiz(id);
        answers.forEach(answer -> userQuizAnswerRepository.deleteById(answer));
    }*/
}

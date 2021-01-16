package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.UserAnswer;
import com.example.mywebquizengine.Repos.UserAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAnswerService  {

    @Autowired
    private UserAnswerRepository userAnswerRepository;


    public void saveAnswer(UserAnswer userAnswer){
        userAnswerRepository.save(userAnswer);
    }

    public Page<UserAnswer> getCompleted (String name, Integer page,
                                          Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());

        return userAnswerRepository.getCompleteAnswersForUser(name, paging);
    }

    public Page<UserAnswer> getAnswersById (int id, Integer page,
                                            Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());

        return userAnswerRepository.getAnswersOnMyQuiz(id, paging);
    }

    public Double getStatistics(Integer id) {
        return ((double)userAnswerRepository.getTrueAnswers(id)/(double)userAnswerRepository.getCountById(id)) * 100;
    }

    public void deleteAnswer(Integer id) {
        List<Integer> answers;
        answers = userAnswerRepository.getAnswerIdForQuiz(id);
        for (int i = 0; i < answers.size(); i++) {
            userAnswerRepository.deleteById(answers.get(i));
        }
    }
}

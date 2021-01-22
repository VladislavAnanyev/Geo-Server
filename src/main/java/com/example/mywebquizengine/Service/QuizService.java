package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.Quiz;
import com.example.mywebquizengine.Repos.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;


@Service
public class QuizService   {

    @Autowired
    private QuizRepository quizRepository;

    public void saveQuiz(Quiz quiz) {
        quiz.setTitle(quiz.getTitle().replace("<","|"));
        quizRepository.save(quiz);
    }

    public ArrayList<Quiz> reloadQuiz() {
        return (ArrayList<Quiz>) quizRepository.findAll();
    }


    public void deleteQuiz(int id) {
        if (quizRepository.findById(id).isPresent()) {
            quizRepository.deleteById(id);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void updateQuiz(int id, String title, String text, ArrayList<String> options, ArrayList<Integer> answers) {

        quizRepository.updateQuizById(id, title, text);
        quizRepository.deleteAnswers(id);
        quizRepository.deleteOptions(id);

        options.forEach(option -> quizRepository.insertOptions(id, option));
        answers.forEach(answer -> quizRepository.insertAnswers(id, answer));

    }

    public Quiz findQuiz(int id){
        if (quizRepository.findById(id).isPresent()){
            return quizRepository.findById(id).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Page<Quiz> getAllQuizzes(Integer page, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy));
        return quizRepository.findAll(paging);

    }

    public Page<Quiz> getMyQuiz (String name, Integer page, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        return quizRepository.getQuizForThis(name, paging);
    }
}

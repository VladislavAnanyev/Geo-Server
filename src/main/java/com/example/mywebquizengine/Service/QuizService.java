package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.Quiz;
import com.example.mywebquizengine.Model.UserAnswer;
import com.example.mywebquizengine.Repos.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


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

    /*public Iterable<Quiz> reloadQuiz() {
        return quizRepository.findAll();
    }*/

    public void deleteQuiz(int id) {
        if (quizRepository.findById(id).isPresent()) {

            quizRepository.deleteById(id);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void updateQuiz(int id, String title, String text, ArrayList<String> options, ArrayList<Integer> answer) {

        Quiz oldQuiz = new Quiz();
        if (quizRepository.findById(id).isPresent()){
            oldQuiz = quizRepository.findById(id).get();
        }

        List<String> oldOpt = oldQuiz.getOptions();

        quizRepository.updateQuizById(id, title, text);

        quizRepository.deleteAnswers(id);

        for (int i = 0; i < answer.size(); i++) {
            quizRepository.insertAnswers(id, answer.get(i));
        }

        quizRepository.deleteOptions(id);

        //quizRepository.updateQuizAnswerById(id, answer);

        for (int i = 0; i < options.size(); i++) {
            quizRepository.insertOptions(id, options.get(i));
        }

       /* quizRepository.updateQuizOptionsById(id, options.get(0), oldOpt.get(0));
        quizRepository.updateQuizOptionsById(id, options.get(1), oldOpt.get(1));
        quizRepository.updateQuizOptionsById(id, options.get(2), oldOpt.get(2));
        quizRepository.updateQuizOptionsById(id, options.get(3), oldOpt.get(3));*/
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

    public Page<Quiz> getMyQuiz (String name, Integer page,
                                          Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());

        return quizRepository.getQuizForThis(name, paging);

    }
}

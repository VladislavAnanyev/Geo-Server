package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Projection.ReceivedRequestView;

import com.example.mywebquizengine.Model.Projection.SentRequestView;
import com.example.mywebquizengine.Model.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface RequestRepository extends CrudRepository<Request, Long> {

    ReceivedRequestView findRequestById(Long id);

    ArrayList<ReceivedRequestView> findAllByToUsernameAndStatus(String username, String status);

    ArrayList<SentRequestView> findAllBySenderUsernameAndStatus(String username, String status);
}

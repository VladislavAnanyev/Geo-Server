package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Projection.RequestView;
import com.example.mywebquizengine.Model.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface RequestRepository extends CrudRepository<Request, Long> {


    ArrayList<RequestView> findAllByToUsernameAndStatus(String username, String status);

}

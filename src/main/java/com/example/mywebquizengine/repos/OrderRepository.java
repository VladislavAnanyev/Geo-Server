package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer>, JpaRepository<Order, Integer> {

}

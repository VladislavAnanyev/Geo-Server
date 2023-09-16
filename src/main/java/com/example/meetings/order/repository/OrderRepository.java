package com.example.meetings.order.repository;

import com.example.meetings.order.model.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer>, JpaRepository<Order, Integer> {
}

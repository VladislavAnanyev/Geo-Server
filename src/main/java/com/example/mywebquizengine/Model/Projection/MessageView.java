package com.example.mywebquizengine.Model.Projection;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Calendar;

public interface MessageView {
    Integer getId();
    String getContent();
    UserView getSender();
    Calendar getTimestamp();
    //DialogView getDialog();
}

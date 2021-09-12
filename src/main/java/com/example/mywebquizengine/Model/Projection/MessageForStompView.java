package com.example.mywebquizengine.Model.Projection;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Calendar;

public interface MessageForStompView {
    Integer getId();
    String getContent();
    UserView getSender();
    Calendar getTimestamp();
    DialogForStomp getDialog();
    //Long getDialogId();

}

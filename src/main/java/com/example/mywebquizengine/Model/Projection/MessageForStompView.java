package com.example.mywebquizengine.Model.Projection;

import java.util.Calendar;

public interface MessageForStompView {
    String getContent();
    UserView getSender();
    Calendar getTimestamp();
    DialogView getDialog();
}

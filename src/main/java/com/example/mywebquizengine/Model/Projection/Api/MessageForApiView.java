package com.example.mywebquizengine.Model.Projection.Api;

import com.example.mywebquizengine.Model.Projection.Api.DialogForApi;
import com.example.mywebquizengine.Model.Projection.UserCommonView;

import java.util.Calendar;

public interface MessageForApiView {
    Integer getId();
    String getContent();
    UserCommonView getSender();
    Calendar getTimestamp();
    DialogForApi getDialog();
    //Long getDialogId();
}



package com.example.mywebquizengine.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectionUtil {

    @Autowired
    private SpelAwareProxyProjectionFactory pf;

    public <T> T parseToProjection(Object obj, Class<T> clazz) {
        return pf.createProjection(clazz, obj);
    }

    public <T> List<T> parseToProjectionList(List obj, Class<T> clazz) {
        List<T> messageViews = new ArrayList<>();

        for (Object o : obj) {
            messageViews.add(pf.createProjection(clazz, o));
        }
        return messageViews;
    }

}

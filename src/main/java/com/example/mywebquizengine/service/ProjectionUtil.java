package com.example.mywebquizengine.service;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.ArrayList;
import java.util.List;

public class ProjectionUtil {
    public static <T> T parseToProjection(Object obj, Class<T> clazz) {
        ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
        return pf.createProjection(clazz, obj);
    }

    public static <T> List<T> parseToProjectionList(List obj, Class<T> clazz) {
        List<T> messageViews = new ArrayList<>();
        ProjectionFactory pf = new SpelAwareProxyProjectionFactory();

        for (Object o : obj) {
            messageViews.add(pf.createProjection(clazz, o));
        }
        return messageViews;
    }

}

package com.example.mywebquizengine.service;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.beans.PropertyDescriptor;
import java.util.List;

public class ProjectionUtil {
    public static <T> T parseToProjection(Object obj, Class<T> clazz) {
        ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
        List<PropertyDescriptor> inputProperties = pf.getProjectionInformation(clazz).getInputProperties();
        return pf.createProjection(clazz, obj);
    }
}

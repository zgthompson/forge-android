package com.pockwester.forge.models;

/**
 * Created by zack on 11/17/13.
 */
public abstract class CourseIdentifier implements TwoLine {
    String id;
    String title;
    String subjectNo;

    public CourseIdentifier(String title, String subjectNo, String id) {
        this.id = id;
        this.title = title;
        this.subjectNo = subjectNo;
    }

    @Override
    public String getLineOne() {
        return subjectNo;
    }

    @Override
    public String getLineTwo() {
        return title;
    }

    @Override
    public String getId() {
        return id;
    }
}

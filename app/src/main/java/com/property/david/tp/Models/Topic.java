package com.property.david.tp.Models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by david on 11/8/17.
 */

public class Topic {
    private String title;
    private Mark myMark;
    private List<Mark> markList;

    public Topic() {
        this.title = "";
        this.myMark = null;
        this.markList = new ArrayList<>();
    }


    public Topic (Topic clone) {
        this.title = clone.getTitle();
        this.myMark = new Mark(clone.getMyMark());
        this.markList = new ArrayList<>(clone.getMarkList());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Mark getMyMark() {
        return myMark;
    }

    public void setMyMark(Mark myMark) {
        this.myMark = myMark;
    }

    public List<Mark> getMarkList() {
        return markList;
    }

    public void setMarkList(List<Mark> markList) {
        this.markList = markList;
    }
}

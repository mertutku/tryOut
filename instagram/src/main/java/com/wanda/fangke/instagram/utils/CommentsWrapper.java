package com.wanda.fangke.instagram.utils;

import org.jinstagram.entity.common.Comments;

import java.io.Serializable;

/**
 * Created by mert.tunc on 24-Jul-15.
 */
public class CommentsWrapper implements Serializable{

    Comments comments;

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }



}

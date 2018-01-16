package com.cvter.nynote.model;

/**
 * Created by serenefang on 2017/6/5.
 * 笔记bean类
 */

public class NoteInfo {

    private String noteName;
    private String notePath;
    private String notePic;

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public void setNotePath(String notePath) {
        this.notePath = notePath;
    }

    public void setNotePic(String notePic) {
        this.notePic = notePic;
    }

    public String getNoteName() {
        return noteName;
    }

    public String getNotePath() {
        return notePath;
    }

    public String getNotePic() {
        return notePic;
    }
}

package com.chanb.zoos;

public class Comment_item {
    private String board_no, re_no, re_comment, re_time, re_writer, re_writer_id, profile, time;

    public Comment_item(String boardNo, String reNo, String reComment, String reTime, String reWriter, String reWriter_id, String profile, String time) {
        this.board_no = boardNo;
        this.re_no = reNo;
        this.re_comment = reComment;
        this.re_time = reTime;
        this.re_writer = reWriter;
        this.re_writer_id = reWriter_id;
        this.profile = profile;
        this.time = time;
    }

    public String getBoard_no() {
        return board_no;
    }

    public void setBoard_no(String boardNo) {
        this.board_no = boardNo;
    }

    public String getRe_no() {
        return re_no;
    }

    public void setRe_no(String reNo) {
        this.re_no = reNo;
    }

    public String getRe_comment() {
        return re_comment;
    }

    public void setRe_comment(String reComment) {
        this.re_comment = reComment;
    }

    public String getRe_time() {
        return re_time;
    }

    public void setRe_time(String reTime) {
        this.re_time = reTime;
    }

    public String getRe_writer() {
        return re_writer;
    }

    public void setRe_writer(String reWriter) {
        this.re_writer = reWriter;
    }

    public String getRe_writer_id() {
        return re_writer_id;
    }

    public String getProfile() {
        return profile;
    }

    public String getTime() {
        return time;
    }


}

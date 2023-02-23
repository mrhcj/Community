package com.community.community.entity;

public class Page {

    private int current = 1;

    private int limit = 10;

    //总行数
    private int rows;

    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页的起始行
    public int getOffset(){
        return (current - 1) * limit;
    }

    //获取总页数
    public int getTotal(){
        return rows%limit==0 ? rows/limit : (rows/limit)+1;
    }

    //获取起始页码
    public int getFrom(){
        return Math.max(current - 2, 1);
    }

    //获取结束页码
    public int getTo(){
        int total = getTotal();
        return Math.min(current + 2, total);
    }

}

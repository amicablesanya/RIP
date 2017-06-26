package com.firstapp.gentle.rip;

/**
 * Created by Gentle on 23-02-2017.
 */
public class Accounts
{
    long rowId;

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    String host;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    String uid;
    String pwd;

    public String getUid() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}

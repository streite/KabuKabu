package com.mairyu.app.kabukabu;

public class Earning {

    private String Company;
    private String Ticker;
    private String Time;
    private String EPS;

    public Earning() {

        Company = "";
        Ticker = "";
        Time = "";
        EPS = "";
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getTicker() {
        return Ticker;
    }

    public void setTicker(String ticker) {
        Ticker = ticker;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getEPS() {
        return EPS;
    }

    public void setEPS(String EPS) {
        this.EPS = EPS;
    }
}

package com.mairyu.app.kabukabu;

public class Stock  {

    private int id;
    private String Company;
    private String Ticker;
    private float Price;
    private String Change;
    private String PercChange;
    private String Category;
    private String Subcategory;
    private int Shares;
    private float Basis;
    private String Date;
    private String Volume;
    private int Commission;
    private String Leverage;
    private int Watch;


    public Stock() {

        id = 0;
        Company = "";
        Ticker = "";
        Price = 0;
        Change = "";
        PercChange = "";
        Category = "";
        Subcategory = "";
        Shares = 0;
        Basis = 0;
        Date = "";
        Volume = "";
        Commission = 0;
        Leverage = "";
        Watch = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public String getChange() {
        return Change;
    }

    public void setChange(String change) {
        Change = change;
    }

    public String getChangePerc() {
        return PercChange;
    }

    public void setPercChange(String percChange) {
        PercChange = percChange;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String group) {
        Category = group;
    }

    public String getSubcategory() {
        return Subcategory;
    }

    public void setSubcategory(String subcategory) {
        Subcategory = subcategory;
    }

    public int getShares() {
        return Shares;
    }

    public void setShares(int shares) {
        Shares = shares;
    }

    public float getBasis() {
        return Basis;
    }

    public void setBasis(float basis) {
        Basis = basis;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        Volume = volume;
    }

    public int getCommission() {
        return Commission;
    }

    public void setCommission(int commission) {
        Commission = commission;
    }

    public String getLeverage() {
        return Leverage;
    }

    public void setLeverage(String leverage) {
        Leverage = leverage;
    }

    public int getWatch() {
        return Watch;
    }

    public void setWatch(int watch) {
        Watch = watch;
    }
}

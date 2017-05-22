package com.mairyu.app.kabukabu;

public class Stock  {

    private int id;
    private String Company;
    private String Ticker;
    private float Price;
    private String Group;
    private int Shares;
    private float Basis;
    private String Date;
    private int Commission;

//    TextView txtStockTicker;
//    TextView txtStockPrice;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.stock);
//
//        txtStockTicker = (TextView) findViewById(R.id.txtStockTicker);
//        txtStockPrice = (TextView) findViewById(R.id.txtStockPrice);
//    }


    public Stock() {

        id = 0;
        Company = "";
        Ticker = "";
        Price = 0;
        Group = "";
        Shares = 0;
        Basis = 0;
        Date = "";
        Commission = 0;
    }

    public Stock(String company, String ticker, float price, String group, int shares, float basis, String date, int commission) {

        id = 0;
        Company = company;
        Ticker = ticker;
        Price = price;
        Group = group;
        Shares = shares;
        Basis = basis;
        Date = date;
        Commission = commission;
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

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
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

    public int getCommission() {
        return Commission;
    }

    public void setCommission(int commission) {
        Commission = commission;
    }
}

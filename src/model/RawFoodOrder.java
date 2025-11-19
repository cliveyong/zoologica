package model;


import java.util.Date;

public class RawFoodOrder {
    private final String o_id;
    private final String contents;
    private final Integer weight;
    private final Date date_received;
    private final Date expiry_date;

    public RawFoodOrder(String o_id, String contents, Integer weight, Date date_recieved, Date expiry_date) {
        this.o_id = o_id;
        this.contents = contents;
        this.weight = weight;
        this.date_received = date_recieved;
        this.expiry_date = expiry_date;
    }

    public String getO_id() {
        return o_id;
    }

    public String getContents() {
        return contents;
    }

    public Integer getWeight() {
        return weight;
    }

    public Date getDate_received() {return date_received;}

    public Date getExpiry_date() {return expiry_date;}
}

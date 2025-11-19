package model;

public class Item {
    private final String i_id;
    private final String p_id;
    private final String name;
    private final int stock;
    private final float price;

    public Item(String i_id, String p_id, String name, int stock, float price) {
        this.i_id = i_id;
        this.p_id = p_id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getI_id() {
        return i_id;
    }

    public String getP_id() {
        return p_id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public float getPrice() {
        return price;
    }
}

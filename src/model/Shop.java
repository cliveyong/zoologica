package model;

public class Shop extends Place {
    private final String type;

    public Shop(String p_id, String name, String type) {
        super(p_id, name);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

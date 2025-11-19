package model;

public class Computer {
    private final String c_id;
    private final String w_id;
    private final String model;
    private final String manufacturer;
    private final String type;

    public Computer(String c_id, String w_id, String model, String manufacturer, String type) {
        this.c_id = c_id;
        this.w_id = w_id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.type = type;
    }

    public String getC_id() {
        return c_id;
    }

    public String getW_id() {
        return w_id;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getType() {
        return type;
    }
}

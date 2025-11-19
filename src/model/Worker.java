package model;

public class Worker {
    private final String w_id;
    private final String name;
    private final float pay_rate;
    private final String address;
    private final String email;
    private final String phone;

    public Worker(String w_id, String name, float pay_rate, String address, String email, String phone) {
        this.w_id = w_id;
        this.name = name;
        this.pay_rate = pay_rate;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public String getW_id() {
        return w_id;
    }

    public String getName() {
        return name;
    }

    public float getPay_rate() {
        return pay_rate;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}

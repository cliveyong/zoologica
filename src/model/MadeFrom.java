package model;

public class MadeFrom {
    private final String a_id;
    private final String name;
    private final String o_id;

    public MadeFrom(String a_id, String name, String o_id) {
        this.a_id = a_id;
        this.name = name;
        this.o_id = o_id;
    }

    public String getA_id() {
        return a_id;
    }

    public String getName() {
        return name;
    }

    public String getO_id() {
        return o_id;
    }
}


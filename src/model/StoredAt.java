package model;

public class StoredAt {
    private final String a_id;
    private final String name;
    private final String p_id;

    public StoredAt(String a_id, String name, String p_id) {
        this.a_id = a_id;
        this.name = name;
        this.p_id = p_id;
    }

    public String getA_id() {
        return a_id;
    }

    public String getName() {
        return name;
    }

    public String getP_id() {
        return p_id;
    }
}


package model;

public abstract class Place {
    private final String p_id;
    private final String name;

    protected Place(String p_id, String name) {
        this.p_id = p_id;
        this.name = name;
    }

    public String getP_id() {
        return p_id;
    }

    public String getName() {
        return name;
    }
}

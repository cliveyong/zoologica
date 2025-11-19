package model;

public class LocatedAt {
    private final String o_id;
    private final String p_id;

    public LocatedAt(String o_id, String p_id) {
        this.o_id = o_id;
        this.p_id = p_id;
    }

    public String getO_id() {
        return o_id;
    }

    public String getP_id() {
        return p_id;
    }
}

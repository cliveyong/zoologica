package model;

public class AssignedTo {
    private final String w_id;
    private final String p_id;

    public AssignedTo(String w_id, String p_id) {
        this.w_id = w_id;
        this.p_id = p_id;
    }

    public String getW_id() {
        return w_id;
    }

    public String getP_id() {
        return p_id;
    }
}


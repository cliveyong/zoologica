package model;

public class SumWeights {
    private String p_id;
    private String name;
    private int sumWeight;

    public SumWeights(String p_id, String name, int sumWeight) {
        this.p_id = p_id;
        this.name = name;
        this.sumWeight = sumWeight;
    }

    public String getP_id() {
        return p_id;
    }

    public String getName() {
        return name;
    }

    public int getSum() {
        return sumWeight;
    }
}


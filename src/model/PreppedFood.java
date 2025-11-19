package model;

public class PreppedFood {
    private final String a_id;
    private final String name;
    private final float weight;

    public PreppedFood(String a_id, String name, float weight) {
        this.a_id = a_id;
        this.name = name;
        this.weight = weight;
    }

    public String getA_id() {
        return a_id;
    }

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }
}

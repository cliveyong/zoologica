package model;

public class StorageUnit extends Place {
    private final int temperature;

    public StorageUnit(String p_id, String name, int temperature) {
        super(p_id, name);
        this.temperature = temperature;
    }

    public int getTemperature() {
        return temperature;
    }
}

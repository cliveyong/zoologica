package model;

public class Habitat extends Place {
    private final String biome;
    private final int area;
    private final int temperature;
    private final int humidity;

    public Habitat(String p_id, String name, String biome, int area, int temperature, int humidity) {
        super(p_id, name);
        this.biome = biome;
        this.area = area;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public String getBiome() {
        return biome;
    }

    public int getArea() {
        return area;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }
}

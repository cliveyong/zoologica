package model;

public class Animal {
    private final String a_id;
    private final String p_id;
    private final String name;
    private final String species;
    private final String genus;

    public Animal(String a_id, String p_id, String name, String species, String genus) {
        this.a_id = a_id;
        this.p_id = p_id;
        this.name = name;
        this.species = species;
        this.genus = genus;
    }

    public String getA_id() {
        return a_id;
    }

    public String getP_id() {
        return p_id;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getGenus() {
        return genus;
    }
}

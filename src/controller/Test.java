package controller;

import database.DatabaseConnectionHandler;
import exceptions.NotExists;
import model.Animal;
import model.Veterinarian;
import util.Constants;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * command-line harness to verify database connectivity and queries.
 * quick way to confirm DB layer works. not used in actual UI
 */
public final class Test {

    private Test() {
        // prevent instantiation
    }

    public static void main(String[] args) throws SQLException, NotExists {
        DatabaseConnectionHandler db = new DatabaseConnectionHandler();

        String username = System.getenv("USER");
        String password = System.getenv("PASSWORD");

        if (!db.login(username, password)) {
            System.err.println("Could not connect to the database. " +
                    "Check ORACLE_URL, USER, and PASSWORD environment variables.");
            return;
        }

        try {
            
            // ex 1: Animals
            ArrayList<String> animalCols = new ArrayList<>();
            animalCols.add(Constants.A_ID);
            animalCols.add(Constants.P_ID);
            animalCols.add(Constants.NAME);
            animalCols.add(Constants.SPECIES);
            animalCols.add(Constants.GENUS);

            Animal[] animals = db.getAnimalInfo(animalCols);

            System.out.println("=== Animals ===");
            for (Animal a : animals) {
                System.out.printf("%s | habitat=%s | %s (%s, %s)%n",
                        a.getA_id(),
                        a.getP_id(),
                        a.getName(),
                        a.getSpecies(),
                        a.getGenus());
            }

            // ex 2: Veterinarians
            ArrayList<String> vetCols = new ArrayList<>();
            vetCols.add(Constants.W_ID);
            vetCols.add(Constants.NAME);
            vetCols.add(Constants.PAY_RATE);
            vetCols.add(Constants.ADDRESS);
            vetCols.add(Constants.EMAIL);
            vetCols.add(Constants.PHONE);
            vetCols.add(Constants.SPECIALIZATION);

            Veterinarian[] vets = db.getVeterinarianInfo(vetCols);

            System.out.println("\n=== Veterinarians ===");
            for (Veterinarian v : vets) {
                System.out.printf("%s | %s | $%.2f/hr | %s | %s | %s | %s%n",
                        v.getW_id(),
                        v.getName(),
                        v.getPay_rate(),
                        v.getAddress(),
                        v.getEmail(),
                        v.getPhone(),
                        v.getSpecialization());
            }

            System.out.println("\nAll tests passed!");

        } finally {
            db.close();
        }
    }
}

package database;

import exceptions.NotExists;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


 // PostgreSQL-specific database connection + query helper.
public class DatabaseConnectionHandler {

    // PostgreSQL connection settings
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/zoo";
    private static final String DB_USER = "zoo";
    private static final String DB_PASSWORD = "zoo";
    private static final String EXCEPTION_TAG = "[EXCEPTION]";

    private Connection connection;

    // ------------------------------------------------------------
    // Connection lifecycle
    // ------------------------------------------------------------

    public boolean login(String username, String password) {
        try {
			if (connection != null) {
				connection.close();
			}

			// Ignore passed-in username/password and use Postgres config
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			connection.setAutoCommit(true);

			System.out.println("\nConnected to PostgreSQL!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            }
        }
    }

    private void ensureConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database connection not established. Call login() first.");
        }
    }

    // ------------------------------------------------------------
    // Animals
    // ------------------------------------------------------------

    // returns all animals joined with their genus
    public Animal[] getAnimalInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT a1.a_id, a1.p_id, a1.name, a1.species, a2.genus " +
                "FROM animals1 a1 " +
                "JOIN animals2 a2 ON a1.species = a2.species " +
                "ORDER BY a1.a_id::integer";

        List<Animal> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String aId = rs.getString("a_id");
                String pId = rs.getString("p_id");
                String name = rs.getString("name");
                String species = rs.getString("species");
                String genus = rs.getString("genus");

                result.add(new Animal(aId, pId, name, species, genus));
            }
        }

        return result.toArray(new Animal[0]);
    }

    public void deleteAnimal(String aId) throws SQLException, NotExists {
        ensureConnection();

        String sql = "DELETE FROM animals1 WHERE a_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, aId);
            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new NotExists("Animal " + aId + " does not exist.");
            }
			
        }catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
    }

    // ------------------------------------------------------------
    // Habitats
    // ------------------------------------------------------------

    public Habitat[] getHabitatInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT h1.p_id, h1.name, h1.biome, h1.area, h2.temperature, h2.humidity " +
                "FROM habitats1 h1 " +
                "JOIN habitats2 h2 ON h1.biome = h2.biome " +
                "ORDER BY h1.p_id::integer";

        List<Habitat> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String pId = rs.getString("p_id");
                String name = rs.getString("name");
                String biome = rs.getString("biome");
                int area = rs.getInt("area");
                int temp = rs.getInt("temperature");
                int humidity = rs.getInt("humidity");

                result.add(new Habitat(pId, name, biome, area, temp, humidity));
            }
        }

        return result.toArray(new Habitat[0]);
    }

    // ------------------------------------------------------------
    // Workers
    // ------------------------------------------------------------

    public Worker[] getWorkerInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT w_id, name, pay_rate, address, email, phone " +
                "FROM workers " +
                "ORDER BY w_id::integer";

        List<Worker> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String wId = rs.getString("w_id");
                String name = rs.getString("name");
                float payRate = rs.getFloat("pay_rate");
                String address = rs.getString("address");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                result.add(new Worker(wId, name, payRate, address, email, phone));
            }
        }

        return result.toArray(new Worker[0]);
    }

    public void updateWorker(String wId, String column, Object value)
            throws SQLException, NotExists {

        ensureConnection();

        // Restrict to a safe subset of columns we actually allow updating.
        String normalized = column.toLowerCase();
        String columnSql;

        switch (normalized) {
            case "address":
                columnSql = "address";
                break;
            case "email":
                columnSql = "email";
                break;
            case "phone":
                columnSql = "phone";
                break;
            case "pay_rate":
                columnSql = "pay_rate";
                break;
            default:
                throw new IllegalArgumentException("Unsupported worker column: " + column);
        }

        String sql = "UPDATE workers SET " + columnSql + " = ? WHERE w_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if ("pay_rate".equals(columnSql)) {
                if (value instanceof Number) {
                    ps.setFloat(1, ((Number) value).floatValue());
                } else {
                    ps.setFloat(1, Float.parseFloat(value.toString()));
                }
            } else {
                ps.setString(1, value.toString());
            }

            ps.setString(2, wId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new NotExists("Worker " + wId + " does not exist.");
            }
        }catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
    }

    // ------------------------------------------------------------
    // Veterinarians
    // ------------------------------------------------------------

    public Veterinarian[] getVeterinarianInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT w.w_id, w.name, w.pay_rate, w.address, w.email, w.phone, v.specialization " +
                "FROM workers w " +
                "JOIN veterinarians v ON w.w_id = v.w_id " +
                "ORDER BY w.w_id::integer";

        List<Veterinarian> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String wId = rs.getString("w_id");
                String name = rs.getString("name");
                float payRate = rs.getFloat("pay_rate");
                String address = rs.getString("address");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String specialization = rs.getString("specialization");

                result.add(new Veterinarian(
                        wId, name, payRate, address, email, phone, specialization));
            }
        }

        return result.toArray(new Veterinarian[0]);
    }

	// Insert a new veterinarian (also inserts into workers)
	public void insertVeterinarian(String wId, String name, float payRate, String address,
		String email, String phone, String specialization) throws SQLException {
		
			ensureConnection();

		String workerSql =
			"INSERT INTO workers (w_id, name, pay_rate, address, email, phone) " +
			"VALUES (?, ?, ?, ?, ?, ?)";

		String vetSql =
			"INSERT INTO veterinarians (w_id, specialization) " +
			"VALUES (?, ?)";

		// using transaction so that either both inserts happen or none
		boolean oldAutoCommit = connection.getAutoCommit();
		connection.setAutoCommit(false);

		// insert into workers and veterinarians in a single transaction
		try (PreparedStatement workerStmt = connection.prepareStatement(workerSql);
			PreparedStatement vetStmt = connection.prepareStatement(vetSql)) {

			// Insert into workers
			workerStmt.setString(1, wId);
			workerStmt.setString(2, name);
			workerStmt.setFloat(3, payRate);
			workerStmt.setString(4, address);
			workerStmt.setString(5, email);
			workerStmt.setString(6, phone);
			workerStmt.executeUpdate();

			// Insert into veterinarians
			vetStmt.setString(1, wId);
			vetStmt.setString(2, specialization);
			vetStmt.executeUpdate();

			connection.commit();
		} catch (SQLException ex) {
			connection.rollback(); // undo any changes if something goes wrong
			throw ex;
		} finally {
			connection.setAutoCommit(oldAutoCommit);
		}
	}

    // ------------------------------------------------------------
    // Shops & Items
    // ------------------------------------------------------------

    public Shop[] getShopInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql = "SELECT p_id, name, type FROM shops ORDER BY p_id::integer";

        List<Shop> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String pId = rs.getString("p_id");
                String name = rs.getString("name");
                String type = rs.getString("type");

                result.add(new Shop(pId, name, type));
            }
        }

        return result.toArray(new Shop[0]);
    }

    public Item[] getItemInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT i_id, p_id, name, stock, price " +
                "FROM items " +
                "ORDER BY i_id::integer";

        List<Item> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String iId = rs.getString("i_id");
                String pId = rs.getString("p_id");
                String name = rs.getString("name");
                int stock = rs.getInt("stock");
                float price = rs.getFloat("price");

                result.add(new Item(iId, pId, name, stock, price));
            }
        }

        return result.toArray(new Item[0]);
    }

    // ------------------------------------------------------------
    // Storage Units
    // ------------------------------------------------------------

    public StorageUnit[] getStorageUnitInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT p_id, name, temperature " +
                "FROM storage_units " +
                "ORDER BY p_id::integer";

        List<StorageUnit> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String pId = rs.getString("p_id");
                String name = rs.getString("name");
                int temperature = rs.getInt("temperature");

                result.add(new StorageUnit(pId, name, temperature));
            }
        }

        return result.toArray(new StorageUnit[0]);
    }

    // ------------------------------------------------------------
    // Raw Food Orders
    // ------------------------------------------------------------

    public RawFoodOrder[] getRawFoodOrderInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT o_id, contents, weight, date_received, expiry_date " +
                "FROM raw_food_orders " +
                "ORDER BY o_id::integer";

        List<RawFoodOrder> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String oId = rs.getString("o_id");
                String contents = rs.getString("contents");
                int weight = rs.getInt("weight");

                java.sql.Date dr = rs.getDate("date_received");
                java.sql.Date ed = rs.getDate("expiry_date");

                Date dateReceived = (dr != null) ? new Date(dr.getTime()) : null;
                Date expiryDate = (ed != null) ? new Date(ed.getTime()) : null;

                result.add(new RawFoodOrder(oId, contents, weight, dateReceived, expiryDate));
            }
        }

        return result.toArray(new RawFoodOrder[0]);
    }

    // ------------------------------------------------------------
    // Computers
    // ------------------------------------------------------------

    public Computer[] getComputerInfo(ArrayList<String> columns) throws SQLException {
        ensureConnection();

        String sql =
                "SELECT c1.c_id, c1.w_id, c1.model, c2.manufacturer, c2.type " +
                "FROM computers1 c1 " +
                "JOIN computers2 c2 ON c1.model = c2.model " +
                "ORDER BY c1.c_id::integer";

        List<Computer> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String cId = rs.getString("c_id");
                String wId = rs.getString("w_id");
                String model = rs.getString("model");
                String manufacturer = rs.getString("manufacturer");
                String type = rs.getString("type");

                result.add(new Computer(cId, wId, model, manufacturer, type));
            }
        }

        return result.toArray(new Computer[0]);
    }

	public Computer[] searchComputersByManufacturer(String manufacturer) throws SQLException {
		ensureConnection();

		String sql =
			"SELECT c1.c_id, c1.w_id, c1.model, c2.manufacturer, c2.type " +
			"FROM computers1 c1 " +
			"JOIN computers2 c2 ON c1.model = c2.model " +
			"WHERE c2.manufacturer ILIKE ? " +
			"ORDER BY c1.c_id::integer";

		ArrayList<Computer> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, "%" + manufacturer + "%");

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String cId   = rs.getString("c_id");
					String wId   = rs.getString("w_id");
					String model = rs.getString("model");
					String manu  = rs.getString("manufacturer");
					String type  = rs.getString("type");

					list.add(new Computer(cId, wId, model, manu, type));
				}
			}
		}

		return list.toArray(new Computer[0]);
	}

	// ---------------------------------------------------------------------
	// Relationship queries
	// ---------------------------------------------------------------------

	public CohabitatesWith[] getCohabitatesWithInfo(ArrayList<String> columns) throws SQLException {
		ensureConnection();

		String sql = "SELECT a_id1, a_id2 FROM cohabitates_with ORDER BY a_id1::integer, a_id2::integer";
		ArrayList<CohabitatesWith> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String a1 = rs.getString("a_id1");
				String a2 = rs.getString("a_id2");
				list.add(new CohabitatesWith(a1, a2));
			}
		}

		return list.toArray(new CohabitatesWith[0]);
	}

	public MaintainsHealthOf[] getMaintainsHealthOfInfo(ArrayList<String> columns) throws SQLException {
		ensureConnection();

		String sql = "SELECT w_id, a_id FROM maintains_health_of ORDER BY w_id::integer, a_id::integer";
		ArrayList<MaintainsHealthOf> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String wId = rs.getString("w_id");
				String aId = rs.getString("a_id");
				list.add(new MaintainsHealthOf(wId, aId));
			}
		}

		return list.toArray(new MaintainsHealthOf[0]);
	}

	public Feeds[] getFeedsInfo(ArrayList<String> columns) throws SQLException {
		ensureConnection();

		String sql = "SELECT w_id, a_id FROM feeds ORDER BY w_id::integer, a_id::integer";
		ArrayList<Feeds> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String wId = rs.getString("w_id");
				String aId = rs.getString("a_id");
				list.add(new Feeds(wId, aId));
			}
		}

		return list.toArray(new Feeds[0]);
	}

	public MadeFrom[] getMadeFromInfo(ArrayList<String> columns) throws SQLException {
		ensureConnection();

		String sql = "SELECT a_id, name, o_id FROM made_from ORDER BY a_id::integer, name, o_id::integer";
		ArrayList<MadeFrom> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String aId = rs.getString("a_id");
				String name = rs.getString("name");
				String oId = rs.getString("o_id");
				list.add(new MadeFrom(aId, name, oId));
			}
		}

		return list.toArray(new MadeFrom[0]);
	}

	// ---------------------------------------------------------------------
	// report queries
	// ---------------------------------------------------------------------

	// Zookeepers who feed every animal in the zoo
	public Zookeeper[] getSuperZookeepers() throws SQLException {
		ensureConnection();

		String sql =
			"SELECT w.w_id, w.name, w.pay_rate, w.address, w.email, w.phone " +
			"FROM zookeepers z " +
			"JOIN workers w ON z.w_id = w.w_id " +
			"WHERE NOT EXISTS ( " +
			"    SELECT 1 FROM animals1 a " +
			"    WHERE NOT EXISTS ( " +
			"        SELECT 1 FROM feeds f " +
			"        WHERE f.w_id = z.w_id AND f.a_id = a.a_id " +
			"    ) " +
			") " +
			"ORDER BY w.w_id::integer";

		ArrayList<Zookeeper> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String wId   = rs.getString("w_id");
				String name  = rs.getString("name");
				float pay    = rs.getFloat("pay_rate");
				String addr  = rs.getString("address");
				String email = rs.getString("email");
				String phone = rs.getString("phone");

				list.add(new Zookeeper(wId, name, pay, addr, email, phone));
			}
		}

		return list.toArray(new Zookeeper[0]);
	}

	// Vets whose pay_rate is <= the average pay for their specialization (for u cheap fucks lol)
	public Veterinarian[] getCheapVeterinarians() throws SQLException {
		ensureConnection();

		String sql =
			"SELECT w.w_id, w.name, v.specialization, w.pay_rate, w.address, w.email, w.phone " +
			"FROM veterinarians v " +
			"JOIN workers w ON v.w_id = w.w_id " +
			"WHERE w.pay_rate <= ALL ( " +
			"    SELECT AVG(w2.pay_rate) " +
			"    FROM workers w2 " +
			"    JOIN veterinarians v2 ON w2.w_id = v2.w_id " +
			"    WHERE v2.specialization = v.specialization " +
			"    GROUP BY v2.specialization " +
			") " +
			"ORDER BY v.specialization, w.pay_rate::integer";

		ArrayList<Veterinarian> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String wId   = rs.getString("w_id");
				String name  = rs.getString("name");
				float pay    = rs.getFloat("pay_rate");
				String addr  = rs.getString("address");
				String email = rs.getString("email");
				String phone = rs.getString("phone");
				String spec  = rs.getString("specialization");

				list.add(new Veterinarian(wId, name, pay, addr, email, phone, spec));
			}
		}

		return list.toArray(new Veterinarian[0]);
	}

	// Total weight of raw food stored in each storage unit
	public SumWeights[] getSumWeights() throws SQLException {
		ensureConnection();

		String sql =
			"SELECT s.p_id, s.name, COALESCE(SUM(o.weight), 0) AS total_weight " +
			"FROM storage_units s " +
			"LEFT JOIN located_at l ON s.p_id = l.p_id " +
			"LEFT JOIN raw_food_orders o ON l.o_id = o.o_id " +
			"GROUP BY s.p_id, s.name " +
			"ORDER BY s.p_id::integer";

		ArrayList<SumWeights> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String pId   = rs.getString("p_id");
				String name  = rs.getString("name");
				int sum      = rs.getInt("total_weight");

				list.add(new SumWeights(pId, name, sum));
			}
		}

		return list.toArray(new SumWeights[0]);
	}

	// get storage units with < 50kg stored
	public SumWeights[] getFreeStorage() throws SQLException {
		ensureConnection();

		String sql =
			"SELECT s.p_id, s.name, COALESCE(SUM(o.weight), 0) AS total_weight " +
			"FROM storage_units s " +
			"LEFT JOIN located_at l ON s.p_id = l.p_id " +
			"LEFT JOIN raw_food_orders o ON l.o_id = o.o_id " +
			"GROUP BY s.p_id, s.name " +
			"HAVING COALESCE(SUM(o.weight), 0) < 50 " +
			"ORDER BY s.p_id::integer";

		ArrayList<SumWeights> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String pId   = rs.getString("p_id");
				String name  = rs.getString("name");
				int sum      = rs.getInt("total_weight");

				list.add(new SumWeights(pId, name, sum));
			}
		}

		return list.toArray(new SumWeights[0]);
	}

}

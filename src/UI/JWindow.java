package UI;

import database.DatabaseConnectionHandler;
import exceptions.NotExists;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;


public class JWindow extends JFrame {

    private final DatabaseConnectionHandler db;

    // core entity tables
    private JTable animalsTable;
    private JTable habitatsTable;
    private JTable workersTable;
    private JTable vetsTable;
    private JTable shopsTable;
    private JTable itemsTable;
    private JTable storageTable;
    private JTable rawOrdersTable;
    private JTable computersTable;

    // relationship tables
    private JTable cohabTable;
    private JTable maintainsTable;
    private JTable feedsTable;
    private JTable madeFromTable;

    // reports table
    private JTable reportsTable;
    private JComboBox<String> reportSelector;

    private JLabel statusLabel;

    // theme colors
    private static final java.awt.Color BG_COLOR = new java.awt.Color(245, 245, 240); // warm neutral
    private static final java.awt.Color ACCENT_COLOR = new java.awt.Color(85, 125, 90); // muted green
    private static final java.awt.Color ACCENT_DARK = new java.awt.Color(65, 95, 70);
    private static final java.awt.Color TEXT_COLOR = new java.awt.Color(40, 40, 40);
    private static final java.awt.Color TABLE_GRID = new java.awt.Color(220, 220, 215);
    private static final java.awt.Color REL_TAB_COLOR    = new java.awt.Color(65, 125, 114); // greenish
    private static final java.awt.Color REPORT_TAB_COLOR = new java.awt.Color(65, 91, 125); // bluish

    // master creation method
    public JWindow() {
        super("Zoo Management System");
        this.db = new DatabaseConnectionHandler();
        initializeDatabase();
        initializeUI();
    }

    private void initializeDatabase() {
        // Uses the credentials already configured inside DatabaseConnectionHandler
        boolean ok = db.login("", "");
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to connect to database. Check your JDBC URL, user, and password.",
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }

    private void initializeUI() {
        // fonts 
        java.awt.Font uiFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);
        javax.swing.UIManager.put("Label.font", uiFont);
        javax.swing.UIManager.put("Button.font", uiFont);
        javax.swing.UIManager.put("Table.font", uiFont);
        javax.swing.UIManager.put("TabbedPane.font", uiFont);
        javax.swing.UIManager.put("TextField.font", uiFont);

        setTitle("Zoologica: Zoo Management System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Zoo Management System\nJava + Swing + PostgreSQL\n\nRefactored for my portfolio.",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        ));

        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        JTabbedPane rootTabs = new JTabbedPane();

        // colors
        getContentPane().setBackground(BG_COLOR);
        UIManager.put("control", BG_COLOR);
        UIManager.put("nimbusBase", ACCENT_DARK);
        UIManager.put("nimbusBlueGrey", ACCENT_COLOR);
        UIManager.put("nimbusLightBackground", BG_COLOR);
        UIManager.put("info", BG_COLOR);
        UIManager.put("nimbusFocus", ACCENT_COLOR);
        UIManager.put("text", TEXT_COLOR);
        UIManager.put("MenuBar.background", BG_COLOR);

        // tabs
        rootTabs.addTab("Workers", createWorkersPanel());
        rootTabs.addTab("Veterinarians", createVetsPanel());
        rootTabs.addTab("Animals", createAnimalsPanel());
        rootTabs.addTab("Habitats", createHabitatsPanel());
        rootTabs.addTab("Raw Food Orders", createRawOrdersPanel());
        rootTabs.addTab("Computers", createComputersPanel());
        rootTabs.addTab("Shops & Items", createShopsItemsPanel());
        rootTabs.addTab("Storage Units", createStoragePanel());
        rootTabs.addTab("Relationships", createRelationshipsPanel());
        rootTabs.addTab("Reports", createReportsPanel());

        // status bar on bottom
        statusLabel = new JLabel("Connected.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new java.awt.Color(245, 245, 245));

        // color the relationships and reports tabs a different color
        int relIndex = rootTabs.indexOfTab("Relationships");
        if (relIndex != -1) {
            rootTabs.setTabComponentAt(relIndex,
                    createStyledTabLabel("Relationships", REL_TAB_COLOR));
        }

        int reportsIndex = rootTabs.indexOfTab("Reports");
        if (reportsIndex != -1) {
            rootTabs.setTabComponentAt(reportsIndex,
                    createStyledTabLabel("Reports", REPORT_TAB_COLOR));
        }

        add(rootTabs, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // load all the tabs
        loadAnimals();
        loadHabitats();
        loadWorkers();
        loadVets();
        loadShops();
        loadItems();
        loadStorageUnits();
        loadRawOrders();
        loadComputers();
        loadRelationships();
        loadDefaultReport();

        setVisible(true);
    }

    // ----------------------------------------------------------------------
    // Animals
    // ----------------------------------------------------------------------

    private JPanel createAnimalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // table
        animalsTable = new JTable();
        styleTable(animalsTable);
        JScrollPane scrollPane = new JScrollPane(animalsTable);

        // refresh and delete controls
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAnimals());
        
        JLabel deleteLabel = new JLabel("Delete Animal ID:");
        JTextField deleteField = new JTextField(8);
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener((ActionEvent e) -> deleteAnimal(deleteField.getText().trim()));

        // add all the controls to a panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);
        controls.add(Box.createHorizontalStrut(16));
        controls.add(deleteLabel);
        controls.add(deleteField);
        controls.add(deleteBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadAnimals() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("a_id");
        cols.add("p_id");
        cols.add("name");
        cols.add("species");
        cols.add("genus");

        try {
            Animal[] animals = db.getAnimalInfo(cols);
            String[] headers = {"Animal ID", "Habitat ID", "Name", "Species", "Genus"};
            Object[][] data = new Object[animals.length][headers.length];

            for (int i = 0; i < animals.length; i++) {
                Animal a = animals[i];
                data[i][0] = a.getA_id();
                data[i][1] = emptyToNA(a.getP_id());
                data[i][2] = emptyToNA(a.getName());
                data[i][3] = emptyToNA(a.getSpecies());
                data[i][4] = emptyToNA(a.getGenus());
            }

            animalsTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + animals.length + " animals.");
        } catch (SQLException ex) {
            showError("Failed to load animals.", ex);
        }
    }

    private void deleteAnimal(String id) {
        if (id == null || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an animal ID.", "Input error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int res = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete animal " + id + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (res != JOptionPane.YES_OPTION) {
            return;
        }

        // attempt to delete the animal, provide feedback on success or failure
        try {
            db.deleteAnimal(id);
            setStatus("Deleted animal " + id + ".");
            loadAnimals();
        } catch (NotExists e) {
            JOptionPane.showMessageDialog(this, "No animal with ID " + id + " exists.",
                    "Not found", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Failed to delete animal " + id + ".", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Habitats
    // ----------------------------------------------------------------------

    private JPanel createHabitatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        habitatsTable = new JTable();
        styleTable(habitatsTable);
        JScrollPane scrollPane = new JScrollPane(habitatsTable);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadHabitats());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadHabitats() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("p_id");
        cols.add("name");
        cols.add("biome");
        cols.add("area");
        cols.add("temperature");
        cols.add("humidity");

        try {
            Habitat[] habitats = db.getHabitatInfo(cols);
            String[] headers = {"Habitat ID", "Name", "Biome", "Area (m²)", "Temp (°C)", "Humidity (%)"};
            Object[][] data = new Object[habitats.length][headers.length];

            for (int i = 0; i < habitats.length; i++) {
                Habitat h = habitats[i];
                data[i][0] = h.getP_id();
                data[i][1] = emptyToNA(h.getName());
                data[i][2] = emptyToNA(h.getBiome());
                data[i][3] = h.getArea();
                data[i][4] = h.getTemperature();
                data[i][5] = h.getHumidity();
            }

            habitatsTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + habitats.length + " habitats.");
        } catch (SQLException ex) {
            showError("Failed to load habitats.", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Workers
    // ----------------------------------------------------------------------

    private JPanel createWorkersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        workersTable = new JTable();
        styleTable(workersTable);
        JScrollPane scrollPane = new JScrollPane(workersTable);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadWorkers());

        JLabel idLabel = new JLabel("Worker ID:");
        JTextField idField = new JTextField(6);

        JLabel fieldLabel = new JLabel("Field:");
        String[] fields = {"Address", "Email", "Phone", "Pay Rate"};
        JComboBox<String> fieldCombo = new JComboBox<>(fields);

        JLabel valueLabel = new JLabel("New value:");
        JTextField valueField = new JTextField(12);

        JButton updateBtn = new JButton("Update");

        updateBtn.addActionListener(e -> updateWorker(
                idField.getText().trim(),
                (String) fieldCombo.getSelectedItem(),
                valueField.getText().trim()
        ));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);
        controls.add(Box.createHorizontalStrut(16));
        controls.add(idLabel);
        controls.add(idField);
        controls.add(fieldLabel);
        controls.add(fieldCombo);
        controls.add(valueLabel);
        controls.add(valueField);
        controls.add(updateBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadWorkers() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("w_id");
        cols.add("name");
        cols.add("pay_rate");
        cols.add("address");
        cols.add("email");
        cols.add("phone");

        try {
            Worker[] workers = db.getWorkerInfo(cols);
            String[] headers = {"Worker ID", "Name", "Pay rate", "Address", "Email", "Phone"};
            Object[][] data = new Object[workers.length][headers.length];

            for (int i = 0; i < workers.length; i++) {
                Worker w = workers[i];
                data[i][0] = w.getW_id();
                data[i][1] = emptyToNA(w.getName());
                data[i][2] = w.getPay_rate();
                data[i][3] = emptyToNA(w.getAddress());
                data[i][4] = emptyToNA(w.getEmail());
                data[i][5] = emptyToNA(w.getPhone());
            }

            workersTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + workers.length + " workers.");
        } catch (SQLException ex) {
            showError("Failed to load workers.", ex);
        }
    }

    private void updateWorker(String wId, String fieldLabel, String newValue) {
        if (wId.isEmpty() || newValue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Worker ID and new value are required.",
                    "Input error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String column;
        Object value;

        // maintain a mapping from user-friendly field names to database column names and value types
        switch (fieldLabel) {
            case "Address":
                column = "address";
                value = newValue;
                break;
            case "Email":
                column = "email";
                value = newValue;
                break;
            case "Phone":
                column = "phone";
                value = newValue;
                break;
            case "Pay Rate":
                column = "pay_rate";
                try {
                    value = Float.parseFloat(newValue);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Pay rate must be a valid number.",
                            "Input error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown field selected.",
                        "Input error",
                        JOptionPane.WARNING_MESSAGE);
                return;
        }

        // try to apply the update, show success or error message
        try {
            db.updateWorker(wId, column, value);
            setStatus("Updated worker " + wId + " (" + fieldLabel + ").");
            loadWorkers();
        } catch (NotExists e) {
            JOptionPane.showMessageDialog(this,
                    "No worker with ID " + wId + " exists.",
                    "Not found",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Failed to update worker " + wId + ".", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Veterinarians
    // ----------------------------------------------------------------------

    private JPanel createVetsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        vetsTable = new JTable();
        styleTable(vetsTable);
        JScrollPane scrollPane = new JScrollPane(vetsTable);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadVets());

        JButton addNewVetBtn = new JButton("Insert New Vet");
        addNewVetBtn.addActionListener((e -> addNewVet()));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);
        controls.add(addNewVetBtn);
        

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadVets() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("w_id");
        cols.add("name");
        cols.add("pay_rate");
        cols.add("address");
        cols.add("email");
        cols.add("phone");
        cols.add("specialization");

        try {
            Veterinarian[] vets = db.getVeterinarianInfo(cols);
            String[] headers = {"Vet ID", "Name", "Pay rate", "Address", "Email", "Phone", "Specialization"};
            Object[][] data = new Object[vets.length][headers.length];

            // populate the table data, converte nulls to "N/A"
            for (int i = 0; i < vets.length; i++) {
                Veterinarian v = vets[i];
                data[i][0] = v.getW_id();
                data[i][1] = emptyToNA(v.getName());
                data[i][2] = v.getPay_rate();
                data[i][3] = emptyToNA(v.getAddress());
                data[i][4] = emptyToNA(v.getEmail());
                data[i][5] = emptyToNA(v.getPhone());
                data[i][6] = emptyToNA(v.getSpecialization());
            }

            vetsTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + vets.length + " veterinarians.");
        } catch (SQLException ex) {
            showError("Failed to load veterinarians.", ex);
        }
    }

    private void addNewVet() {
        JTextField idField = new JTextField(8);
        JTextField nameField = new JTextField(12);
        JTextField payRateField = new JTextField(8);
        JTextField addressField = new JTextField(12);
        JTextField emailField = new JTextField(12);
        JTextField phoneField = new JTextField(12);
        JTextField specializationField = new JTextField(12);

        // new panel that shows all the input fields for the new vet
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Worker ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Pay Rate:"));
        panel.add(payRateField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Specialization:"));
        panel.add(specializationField);

        // confirmation dialog box. build in method
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Veterinarian",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                db.insertVeterinarian(
                        idField.getText().trim(),
                        nameField.getText().trim(),
                        Float.parseFloat(payRateField.getText().trim()),
                        addressField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        specializationField.getText().trim()
                );
                setStatus("Added new veterinarian " + nameField.getText().trim() + ".");
                loadVets();
            } catch (SQLException ex) {
                showError("Failed to add veterinarian.", ex);
            }
        }
    }

    // ----------------------------------------------------------------------
    // Shops & Items
    // ----------------------------------------------------------------------

    private JPanel createShopsItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        shopsTable = new JTable();
        styleTable(shopsTable);
        itemsTable = new JTable();
        styleTable(itemsTable);

        JPanel tables = new JPanel(new GridLayout(2, 1));
        tables.add(new JScrollPane(shopsTable));
        tables.add(new JScrollPane(itemsTable));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadShops();
            loadItems();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(tables, BorderLayout.CENTER);

        return panel;
    }

    private void loadShops() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("p_id");
        cols.add("name");
        cols.add("type");

        try {
            Shop[] shops = db.getShopInfo(cols);
            String[] headers = {"Shop ID", "Name", "Type"};
            Object[][] data = new Object[shops.length][headers.length];

            for (int i = 0; i < shops.length; i++) {
                Shop s = shops[i];
                data[i][0] = s.getP_id();
                data[i][1] = emptyToNA(s.getName());
                data[i][2] = emptyToNA(s.getType());
            }

            shopsTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + shops.length + " shops.");
        } catch (SQLException ex) {
            showError("Failed to load shops.", ex);
        }
    }

    private void loadItems() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("i_id");
        cols.add("p_id");
        cols.add("name");
        cols.add("stock");
        cols.add("price");

        try {
            Item[] items = db.getItemInfo(cols);
            String[] headers = {"Item ID", "Shop ID", "Name", "Stock", "Price"};
            Object[][] data = new Object[items.length][headers.length];

            for (int i = 0; i < items.length; i++) {
                Item it = items[i];
                data[i][0] = it.getI_id();
                data[i][1] = emptyToNA(it.getP_id());
                data[i][2] = emptyToNA(it.getName());
                data[i][3] = it.getStock();
                data[i][4] = it.getPrice();
            }

            itemsTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + items.length + " items.");
        } catch (SQLException ex) {
            showError("Failed to load items.", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Storage units
    // ----------------------------------------------------------------------

    private JPanel createStoragePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        storageTable = new JTable();
        styleTable(storageTable);
        JScrollPane scrollPane = new JScrollPane(storageTable);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadStorageUnits());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadStorageUnits() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("p_id");
        cols.add("name");
        cols.add("temperature");

        try {
            StorageUnit[] units = db.getStorageUnitInfo(cols);
            String[] headers = {"Storage ID", "Name", "Temperature (°C)"};
            Object[][] data = new Object[units.length][headers.length];

            for (int i = 0; i < units.length; i++) {
                StorageUnit s = units[i];
                data[i][0] = s.getP_id();
                data[i][1] = emptyToNA(s.getName());
                data[i][2] = s.getTemperature();
            }

            storageTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + units.length + " storage units.");
        } catch (SQLException ex) {
            showError("Failed to load storage units.", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Raw food orders
    // ----------------------------------------------------------------------

    private JPanel createRawOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        rawOrdersTable = new JTable();
        styleTable(rawOrdersTable);
        JScrollPane scrollPane = new JScrollPane(rawOrdersTable);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadRawOrders());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadRawOrders() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("o_id");
        cols.add("contents");
        cols.add("weight");
        cols.add("date_received");
        cols.add("expiry_date");

        try {
            RawFoodOrder[] orders = db.getRawFoodOrderInfo(cols);
            String[] headers = {"Order ID", "Contents", "Weight (kg)", "Date received", "Expiry date"};
            Object[][] data = new Object[orders.length][headers.length];

            for (int i = 0; i < orders.length; i++) {
                RawFoodOrder o = orders[i];
                data[i][0] = o.getO_id();
                data[i][1] = emptyToNA(o.getContents());
                data[i][2] = o.getWeight();
                data[i][3] = o.getDate_received();
                data[i][4] = o.getExpiry_date();
            }

            rawOrdersTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + orders.length + " raw food orders.");
        } catch (SQLException ex) {
            showError("Failed to load raw food orders.", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Computers
    // ----------------------------------------------------------------------

    private JPanel createComputersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        computersTable = new JTable();
        styleTable(computersTable);
        JScrollPane scrollPane = new JScrollPane(computersTable);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadComputers());

        JButton searchComputerBtn = new JButton("Search by Model");
        searchComputerBtn.addActionListener(e -> searchComputerManufacturerDialog());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);
        controls.add(searchComputerBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadComputers() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("c_id");
        cols.add("w_id");
        cols.add("model");
        cols.add("manufacturer");
        cols.add("type");

        try {
            Computer[] computers = db.getComputerInfo(cols);
            String[] headers = {"Computer ID", "Worker ID", "Model", "Manufacturer", "Type"};
            Object[][] data = new Object[computers.length][headers.length];

            for (int i = 0; i < computers.length; i++) {
                Computer c = computers[i];
                data[i][0] = c.getC_id();
                data[i][1] = emptyToNA(c.getW_id());
                data[i][2] = emptyToNA(c.getModel());
                data[i][3] = emptyToNA(c.getManufacturer());
                data[i][4] = emptyToNA(c.getType());
            }

            computersTable.setModel(nonEditableModel(data, headers));
            setStatus("Loaded " + computers.length + " computers.");
        } catch (SQLException ex) {
            showError("Failed to load computers.", ex);
        }
    }

    private void searchComputerManufacturerDialog() {
        String manufacturer = JOptionPane.showInputDialog(this, "Enter computer manufacturer to search for:", "Search Computers",
                JOptionPane.PLAIN_MESSAGE);
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            return;
        }
        try {
            Computer[] computers = db.searchComputersByManufacturer(manufacturer.trim());
            String[] headers = {"Computer ID", "Worker ID", "Model", "Manufacturer", "Type"};
            Object[][] data = new Object[computers.length][headers.length];

            for (int i = 0; i < computers.length; i++) {
                Computer c = computers[i];
                data[i][0] = c.getC_id();
                data[i][1] = emptyToNA(c.getW_id());
                data[i][2] = emptyToNA(c.getModel());
                data[i][3] = emptyToNA(c.getManufacturer());
                data[i][4] = emptyToNA(c.getType());
            }

            computersTable.setModel(nonEditableModel(data, headers));
            setStatus("Found " + computers.length + " computers matching model '" + manufacturer.trim() + "'.");
        } catch (SQLException ex) {
            showError("Failed to search computers.", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Relationships
    // ----------------------------------------------------------------------

    private JPanel createRelationshipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane relTabs = new JTabbedPane();

        cohabTable = new JTable();
        maintainsTable = new JTable();
        feedsTable = new JTable();
        madeFromTable = new JTable();

        styleTable(cohabTable);
        styleTable(maintainsTable);
        styleTable(feedsTable);
        styleTable(madeFromTable);

        relTabs.addTab("Cohabitations", new JScrollPane(cohabTable));
        relTabs.addTab("Maintains Health Of", new JScrollPane(maintainsTable));
        relTabs.addTab("Feeds", new JScrollPane(feedsTable));
        relTabs.addTab("Prepared Foods for Animals", new JScrollPane(madeFromTable));

        JButton refreshBtn = new JButton("Refresh all");
        refreshBtn.addActionListener(e -> loadRelationships());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(relTabs, BorderLayout.CENTER);

        return panel;
    }

    private void loadRelationships() {
        loadCohabitations();
        loadMaintainsHealth();
        loadFeeds();
        loadMadeFrom();
    }

    private void loadCohabitations() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("a_id1");
        cols.add("a_id2");

        try {
            CohabitatesWith[] rels = db.getCohabitatesWithInfo(cols);
            String[] headers = {"Animal ID 1", "Animal ID 2"};
            Object[][] data = new Object[rels.length][headers.length];

            for (int i = 0; i < rels.length; i++) {
                CohabitatesWith r = rels[i];
                data[i][0] = r.getA_id1();
                data[i][1] = r.getA_id2();
            }

            cohabTable.setModel(nonEditableModel(data, headers));
        } catch (SQLException ex) {
            showError("Failed to load cohabitations.", ex);
        }
    }

    private void loadMaintainsHealth() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("w_id");
        cols.add("a_id");

        try {
            MaintainsHealthOf[] rels = db.getMaintainsHealthOfInfo(cols);
            String[] headers = {"Vet ID", "Animal ID"};
            Object[][] data = new Object[rels.length][headers.length];

            for (int i = 0; i < rels.length; i++) {
                MaintainsHealthOf r = rels[i];
                data[i][0] = r.getW_id();
                data[i][1] = r.getA_id();
            }

            maintainsTable.setModel(nonEditableModel(data, headers));
        } catch (SQLException ex) {
            showError("Failed to load maintains-health-of.", ex);
        }
    }

    private void loadFeeds() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("w_id");
        cols.add("a_id");

        try {
            Feeds[] rels = db.getFeedsInfo(cols);
            String[] headers = {"Zookeeper ID", "Animal ID"};
            Object[][] data = new Object[rels.length][headers.length];

            for (int i = 0; i < rels.length; i++) {
                Feeds r = rels[i];
                data[i][0] = r.getW_id();
                data[i][1] = r.getA_id();
            }

            feedsTable.setModel(nonEditableModel(data, headers));
        } catch (SQLException ex) {
            showError("Failed to load feeds relationships.", ex);
        }
    }

    private void loadMadeFrom() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("a_id");
        cols.add("name");
        cols.add("o_id");

        try {
            MadeFrom[] rels = db.getMadeFromInfo(cols);
            String[] headers = {"Animal ID", "Prepped food", "Raw order ID"};
            Object[][] data = new Object[rels.length][headers.length];

            for (int i = 0; i < rels.length; i++) {
                MadeFrom r = rels[i];
                data[i][0] = r.getA_id();
                data[i][1] = emptyToNA(r.getName());
                data[i][2] = r.getO_id();
            }

            madeFromTable.setModel(nonEditableModel(data, headers));
        } catch (SQLException ex) {
            showError("Failed to load made-from relationships.", ex);
        }
    }

    // ----------------------------------------------------------------------
    // Reports and advanced queries
    // ----------------------------------------------------------------------

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        reportsTable = new JTable();
        styleTable(reportsTable);
        JScrollPane scrollPane = new JScrollPane(reportsTable); // drop down list basically

        reportSelector = new JComboBox<>(new String[]{
                "Super zookeepers (feed all animals)",
                "Cheapest veterinarians (per specialization)",
                "Storage units with free space (< 50kg total)",
                "Total weight stored per storage unit"
        });

        JButton runBtn = new JButton("Run");
        runBtn.addActionListener(e -> runSelectedReport());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> runSelectedReport());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Report:"));
        controls.add(reportSelector);
        controls.add(runBtn);
        controls.add(refreshBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    

    private void loadDefaultReport() {
        // by default, show nothing. if you want to show something by default, you can call runSelectedReport() here
        // runSelectedReport();
        return;
    }

    private void runSelectedReport() {
        String selected = (String) reportSelector.getSelectedItem();
        if (selected == null) {
            return;
        }

        // check the prefix of the selected report to determine which one to run. this is a bit hacky but it works for our purposes
        // if you want this to be better, maintain a mapping from report names to functions instead of using if-else statements
        try {
            if (selected.startsWith("Super zookeepers")) {
                runSuperZookeepersReport();
            } else if (selected.startsWith("Cheapest veterinarians")) {
                runCheapVetsReport();
            } else if (selected.startsWith("Storage units with free space")) {
                runFreeStorageReport();
            } else if (selected.startsWith("Total weight stored")) {
                runSumWeightsReport();
            }
        } catch (SQLException ex) {
            showError("Failed to run report.", ex);
        }
    }

    private void runSuperZookeepersReport() throws SQLException {
        Zookeeper[] keepers = db.getSuperZookeepers();

        String[] headers = {"Zookeeper ID", "Name", "Pay rate", "Address", "Email", "Phone"};
        Object[][] data = new Object[keepers.length][headers.length];

        for (int i = 0; i < keepers.length; i++) {
            Zookeeper z = keepers[i];
            data[i][0] = z.getW_id();
            data[i][1] = emptyToNA(z.getName());
            data[i][2] = z.getPay_rate();
            data[i][3] = emptyToNA(z.getAddress());
            data[i][4] = emptyToNA(z.getEmail());
            data[i][5] = emptyToNA(z.getPhone());
        }

        reportsTable.setModel(nonEditableModel(data, headers));
        setStatus("Loaded " + keepers.length + " super zookeepers.");
    }

    private void runCheapVetsReport() throws SQLException {
        Veterinarian[] vets = db.getCheapVeterinarians();

        String[] headers = {"Specialization", "Pay rate", "Vet ID", "Name", "Address", "Email", "Phone"};
        Object[][] data = new Object[vets.length][headers.length];

        for (int i = 0; i < vets.length; i++) {
            Veterinarian v = vets[i];
            data[i][0] = emptyToNA(v.getSpecialization());
            data[i][1] = v.getPay_rate();
            data[i][2] = v.getW_id();
            data[i][3] = emptyToNA(v.getName());
            data[i][4] = emptyToNA(v.getAddress());
            data[i][5] = emptyToNA(v.getEmail());
            data[i][6] = emptyToNA(v.getPhone());
            
        }

        reportsTable.setModel(nonEditableModel(data, headers));
        setStatus("Loaded " + vets.length + " cheap veterinarians.");
    }

    private void runFreeStorageReport() throws SQLException {
        SumWeights[] rows = db.getFreeStorage();

        String[] headers = {"Storage ID", "Name", "Total weight (kg)"};
        Object[][] data = new Object[rows.length][headers.length];

        for (int i = 0; i < rows.length; i++) {
            SumWeights sw = rows[i];
            data[i][0] = sw.getP_id();
            data[i][1] = emptyToNA(sw.getName());
            data[i][2] = sw.getSum();
        }

        reportsTable.setModel(nonEditableModel(data, headers));
        setStatus("Loaded " + rows.length + " storage units with < 50kg.");
    }

    private void runSumWeightsReport() throws SQLException {
        SumWeights[] rows = db.getSumWeights();

        String[] headers = {"Storage ID", "Name", "Total weight (kg)"};
        Object[][] data = new Object[rows.length][headers.length];

        for (int i = 0; i < rows.length; i++) {
            SumWeights sw = rows[i];
            data[i][0] = sw.getP_id();
            data[i][1] = emptyToNA(sw.getName());
            data[i][2] = sw.getSum();
        }

        reportsTable.setModel(nonEditableModel(data, headers));
        setStatus("Loaded " + rows.length + " storage weight summaries.");
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    private DefaultTableModel nonEditableModel(Object[][] data, String[] headers) {
        return new DefaultTableModel(data, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private String emptyToNA(String s) {
        return (s == null || s.isEmpty()) ? "N/A" : s;
    }

    private void showError(String message, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                message + "\n\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        setStatus("Error: " + message);
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    // styling

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);    // click headers to sort
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(TABLE_GRID);
        table.setForeground(TEXT_COLOR);
        table.setBackground(BG_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(java.awt.Color.WHITE);

        // Zebra striping
        javax.swing.table.DefaultTableCellRenderer zebra = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                java.awt.Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(java.awt.Color.WHITE);
                    } else {
                        c.setBackground(new java.awt.Color(245, 245, 245));
                    }
                }
                return c;
            }
        };

        // apply zebra renderer to all columns
        table.setDefaultRenderer(Object.class, zebra);

        // nicer header
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(ACCENT_DARK);
        table.getTableHeader().setForeground(java.awt.Color.WHITE);
    }

    private java.awt.Component createStyledTabLabel(String title, java.awt.Color textColor) {
        JLabel label = new JLabel(title);
        label.setForeground(textColor);

        // bold font
        java.awt.Font base = label.getFont();
        label.setFont(base.deriveFont(java.awt.Font.BOLD, base.getSize2D()));

        // Don't paint a background, let Nimbus handle the tab background
        label.setOpaque(false);

        return label;
    }

}

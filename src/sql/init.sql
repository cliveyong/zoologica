-- drop table COMPUTERS1 cascade constraints
-- /
--
-- drop table COMPUTERS2 cascade constraints
-- /
--
-- drop table VENDORS cascade constraints
-- /
--
-- drop table ITEMS cascade constraints
-- /
--
-- drop table WORKS_AT cascade constraints
-- /
--
-- drop table SHOPS cascade constraints
-- /
--
-- drop table ASSIGNED_TO cascade constraints
-- /
--
-- drop table FEEDS cascade constraints
-- /
--
-- drop table ZOOKEEPERS cascade constraints
-- /
--
-- drop table MAINTAINS_HEALTH_OF cascade constraints
-- /
--
-- drop table VETERINARIANS cascade constraints
-- /
--
-- drop table WORKERS cascade constraints
-- /
--
-- drop table COHABITATES_WITH cascade constraints
-- /
--
-- drop table MADE_FROM cascade constraints
-- /
--
-- drop table STORED_AT cascade constraints
-- /
--
-- drop table PREPPED_FOOD cascade constraints
-- /
--
-- drop table ANIMALS1 cascade constraints
-- /
--
-- drop table HABITATS1 cascade constraints
-- /
--
-- drop table HABITATS2 cascade constraints
-- /
--
-- drop table ANIMALS2 cascade constraints
-- /
--
-- drop table LOCATED_AT cascade constraints
-- /
--
-- drop table STORAGE_UNITS cascade constraints
-- /
--
-- drop table RAW_FOOD_ORDERS cascade constraints
-- /



CREATE TABLE Computers2(
    model VARCHAR(20) PRIMARY KEY,
    manufacturer VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL
);

CREATE TABLE Workers(
    w_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    pay_rate FLOAT NOT NULL,
    address VARCHAR(30) NOT NULL,
    email VARCHAR(30) UNIQUE NOT NULL,
    phone VARCHAR(13) UNIQUE NOT NULL
);

CREATE TABLE Computers1(
   c_id VARCHAR(20) PRIMARY KEY,
   w_id VARCHAR(20) UNIQUE,
   model VARCHAR(20) NOT NULL,
   FOREIGN KEY (model) REFERENCES Computers2(model),
   FOREIGN KEY (w_id) REFERENCES Workers(w_id)
);

CREATE TABLE Zookeepers(
    w_id VARCHAR(20) PRIMARY KEY,
    FOREIGN KEY (w_id) REFERENCES Workers(w_id)
);

CREATE TABLE Vendors(
    w_id VARCHAR(20) PRIMARY KEY,
    FOREIGN KEY (w_id) REFERENCES Workers(w_id)
);

CREATE TABLE Veterinarians(
    w_id VARCHAR(20) PRIMARY KEY,
    specialization VARCHAR(20),
    FOREIGN KEY (w_id) REFERENCES Workers(w_id)
);


CREATE TABLE Habitats2(
    biome VARCHAR(50) PRIMARY KEY,
    temperature INTEGER,
    humidity INTEGER
);

CREATE TABLE Habitats1(
    p_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(20),
    biome VARCHAR(50),
    area INTEGER,
    FOREIGN KEY (biome) REFERENCES Habitats2
);

CREATE TABLE Shops(
    p_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(20),
    type VARCHAR(20) NOT NULL
);

CREATE TABLE Storage_Units(
    p_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(20),
    temperature INTEGER NOT NULL
);

CREATE TABLE Items(
    i_id VARCHAR(20) PRIMARY KEY,
    p_id VARCHAR(20) NOT NULL,
    name VARCHAR(30) NOT NULL,
    stock INTEGER NOT NULL,
    price FLOAT NOT NULL,
    FOREIGN KEY (p_id) REFERENCES Shops(p_id)
);

CREATE TABLE Animals2(
    species VARCHAR(20) PRIMARY KEY,
    genus VARCHAR(20)
);

CREATE TABLE Animals1(
    a_id VARCHAR(20) PRIMARY KEY,
    p_id VARCHAR(20),
    name VARCHAR(30),
    species VARCHAR(20),
    FOREIGN KEY (species) REFERENCES Animals2,
    FOREIGN KEY (p_id) REFERENCES Habitats1(p_id)
);

CREATE TABLE Prepped_Food(
    a_id VARCHAR(20),
    name VARCHAR(20),
    weight FLOAT,
    PRIMARY KEY (a_id, name),
    FOREIGN KEY (a_id) REFERENCES Animals1(a_id) ON DELETE CASCADE
);

CREATE TABLE Raw_Food_Orders(
    o_id VARCHAR(20) PRIMARY KEY,
    contents VARCHAR(50) NOT NULL,
    weight INTEGER NOT NULL,
    date_received DATE NOT NULL,
    expiry_date DATE NOT NULL
 );

CREATE TABLE Works_at(
    w_id VARCHAR(20),
    p_id VARCHAR(20),
    PRIMARY KEY (w_id, p_id),
    FOREIGN KEY (w_id) REFERENCES Workers,
    FOREIGN KEY (p_id) REFERENCES Shops
);

CREATE TABLE Assigned_to(
	w_id VARCHAR(20),
	p_id VARCHAR(20),
	PRIMARY KEY(w_id, p_id),
	FOREIGN KEY (w_id) REFERENCES Zookeepers(w_id),
	FOREIGN KEY (p_id) REFERENCES Habitats1(p_id)
);

CREATE TABLE Feeds(
    w_id VARCHAR(20),
    a_id VARCHAR(20),
    PRIMARY KEY (w_id, a_id),
    FOREIGN KEY (w_id) REFERENCES  Zookeepers(w_id),
    FOREIGN KEY (a_id) REFERENCES Animals1(a_id) ON DELETE CASCADE
);

CREATE TABLE Maintains_Health_of(
    w_id VARCHAR(20),
    a_id VARCHAR(20),
    PRIMARY KEY (w_id, a_id),
    FOREIGN KEY (w_id) REFERENCES Veterinarians(w_id),
    FOREIGN KEY (a_id) REFERENCES Animals1(a_id) ON DELETE CASCADE
);

CREATE TABLE Cohabitates_with(
    a_id1 VARCHAR(20),
    a_id2 VARCHAR(20),
    PRIMARY KEY(a_id1, a_id2),
    FOREIGN KEY (a_id1) REFERENCES Animals1(a_id) ON DELETE CASCADE,
    FOREIGN KEY (a_id2) REFERENCES Animals1(a_id) ON DELETE CASCADE
);

CREATE TABLE Made_from(
	a_id VARCHAR(20),
	name VARCHAR(30),
	o_id VARCHAR(20),
	PRIMARY KEY(a_id, name, o_id),
	FOREIGN KEY (a_id, name) REFERENCES Prepped_Food(a_id, name) ON DELETE CASCADE,
	FOREIGN KEY (o_id) REFERENCES Raw_Food_Orders(o_id)
);

CREATE TABLE Stored_at(
    a_id VARCHAR(20),
    name VARCHAR(30),
    p_id VARCHAR(20),
    PRIMARY KEY(a_id, name, p_id),
    FOREIGN KEY (a_id, name) REFERENCES Prepped_Food(a_id, name) ON DELETE CASCADE,
    FOREIGN KEY (p_id) REFERENCES Storage_Units(p_id)
);

CREATE TABLE Located_at(
    o_id VARCHAR(20),
    p_id VARCHAR(20),
    PRIMARY KEY(o_id, p_id),
    FOREIGN KEY (o_id) REFERENCES Raw_Food_Orders(o_id),
    FOREIGN KEY (p_id) REFERENCES Storage_Units(p_id)
);

INSERT
INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('1', 'Clive Yong', 15.40, '1234 noname street', 'cliveyong@domain.com', '6041231234');

INSERT
INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('2', 'Mana Longhenry', 16.00, '4321 thisthing street', 'manalong@domain.com', '6042468100');

INSERT
INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('3', 'Skye Joe', 12.34, '2468 somewhere way', 'SkyeJoe@domain.com','5852111212');

INSERT
INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('4', 'Bob Way', 15.40, '1234 noname street','bobway@domain.com', '6041231233');

INSERT
INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('5', 'Richard Han', 15.40, '3579 somewhere place', 'richardh@domain.com','6043215678');

INSERT
INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('6', 'Steve Irwin', 30.51, 'Australia', 'steve@domain.com','9999999999');

INSERT INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('7', 'Daniel Yuan', 18.00, '3579 somewhere place', 'daniel@domain.com', '6049876543');

INSERT INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('8', 'Mia Park', 17.50, '2468 new street', 'miapark@domain.com', '6042345678');

INSERT INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('9', 'Lisa Kim', 14.00, '2468 Elm St', 'lisa.kim@domain.com', '5551234567');

INSERT INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('10', 'Daniel Park', 14.50, 'blah St', 'danielpark@domain.com', '6043216543');

INSERT INTO Workers(w_id, name, pay_rate, address, email, phone)
VALUES ('11', 'Alex Kim', 15.00, '789 3rd Ave', 'alex.kim@domain.com', '6047891234');



INSERT
INTO Computers2(model, manufacturer, type)
VALUES ('Macbook Air 2020', 'Apple', 'Laptop');

INSERT
INTO Computers2(model, manufacturer, type)
VALUES ('Macbook Air 2019', 'Apple', 'Laptop');

INSERT
INTO Computers2(model, manufacturer, type)
VALUES ('Macbook Air 2018', 'Apple', 'Laptop');

INSERT
INTO Computers2(model, manufacturer, type)
VALUES ('Macbook Air 2017', 'Apple', 'Laptop');

INSERT
INTO Computers2(model, manufacturer, type)
VALUES ('Macbook Air 2016', 'Apple', 'Laptop');

INSERT
INTO Computers2(model, manufacturer, type)
VALUES ('Dell XPS 13', 'Dell', 'Desktop');

INSERT
INTO Computers1(c_id, w_id, model)
VALUES ('1', '1', 'Macbook Air 2020');

INSERT
INTO Computers1(c_id, w_id, model)
VALUES ('2', '2', 'Macbook Air 2019');

INSERT
INTO Computers1(c_id, w_id, model)
VALUES ('3', '3', 'Macbook Air 2018');

INSERT
INTO Computers1(c_id, w_id, model)
VALUES ('4', '4', 'Macbook Air 2017');

INSERT
INTO Computers1(c_id, w_id, model)
VALUES ('5', '5', 'Macbook Air 2016');

INSERT
INTO Computers1(c_id, w_id, model)
VALUES ('6', NULL, 'Dell XPS 13');

INSERT
INTO Computers1(c_id, w_id, model)
VALUES ('7', NULL, 'Dell XPS 13');

INSERT
INTO Zookeepers(w_id)
VALUES ('1');

INSERT
INTO Zookeepers(w_id)
VALUES ('2');

INSERT
INTO Zookeepers(w_id)
VALUES ('3');

INSERT
INTO Zookeepers(w_id)
VALUES ('4');

INSERT
INTO Zookeepers(w_id)
VALUES ('5');

INSERT
INTO Zookeepers(w_id)
VALUES ('6');

INSERT
INTO Vendors(w_id)
VALUES ('1');

INSERT
INTO Vendors(w_id)
VALUES ('2');

INSERT
INTO Vendors(w_id)
VALUES ('3');

INSERT
INTO Vendors(w_id)
VALUES ('4');

INSERT
INTO Vendors(w_id)
VALUES ('5');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('1', 'large felines');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('2', 'small felines');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('3', 'birds');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('4', 'reptiles');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('5', 'bears');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('7', 'large felines');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('8', 'small felines');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('9', 'birds');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('10', 'reptiles');

INSERT
INTO Veterinarians(w_id, specialization)
VALUES ('11', 'bears');

INSERT
INTO Habitats2(biome, temperature, humidity)
VALUES ('Asian Taiga', 32, 70);

INSERT
INTO Habitats2(biome, temperature, humidity)
VALUES ('African Savanna', 30, 20);

INSERT
INTO Habitats2(biome, temperature, humidity)
VALUES ('Antarctic Tundra', -20, 1);

INSERT
INTO Habitats2(biome, temperature, humidity)
VALUES ('African Grasslands', 28, 25);

INSERT
INTO Habitats2(biome, temperature, humidity)
VALUES ('North American Woodlands', 25, 70);

INSERT
INTO Habitats1(p_id, name, biome, area)
VALUES ('001', 'Tiger Habitat', 'Asian Taiga', 37);

INSERT
INTO Habitats1(p_id, name, biome, area)
VALUES ('002', 'Lion Habitat', 'African Savanna', 27);

INSERT
INTO Habitats1(p_id, name, biome, area)
VALUES ('003', 'Penguin Habitat', 'Antarctic Tundra', 20);

INSERT
INTO Habitats1(p_id, name, biome, area)
VALUES ('004', 'Giraffe Habitat', 'African Grasslands', 40);

INSERT
INTO Habitats1(p_id, name, biome, area)
VALUES ('005', 'Grizzly Bear Habitat', 'North American Woodlands', 40 );

INSERT
INTO Shops(p_id, name, type)
VALUES ('101', 'Clothing Store', 'Clothing');

INSERT
INTO Shops(p_id, name, type)
VALUES ('102', 'Drinks Store', 'Drinks');

INSERT
INTO Shops(p_id, name, type)
VALUES ('103', 'Plushy Store', 'Stuffed Animals');

INSERT
INTO Shops(p_id, name, type)
VALUES ('104', 'Balloon Store', 'Balloons');

INSERT
INTO Shops(p_id, name, type)
VALUES ('105', 'Food Store', 'Food');

INSERT
INTO Storage_Units(p_id, name, temperature)
VALUES ('201', 'Unit 1', 2);

INSERT
INTO Storage_Units(p_id, name, temperature)
VALUES ('202', 'Unit 2', -20);

INSERT
INTO Storage_Units(p_id, name, temperature)
VALUES ('203', 'Unit 3', 15);

INSERT
INTO Storage_Units(p_id, name, temperature)
VALUES ('204', 'Unit 4', 15);

INSERT
INTO Storage_Units(p_id, name, temperature)
VALUES ('205', 'Unit 5', 5);

INSERT
INTO Items(i_id, p_id, name, stock, price)
VALUES ('0001', '101', 'T-Shirt', 50, 24.99);

INSERT
INTO Items(i_id, p_id, name, stock, price)
VALUES ('0002', '102', 'Soda Bottle', 20, 3.50);


INSERT
INTO Items(i_id, p_id, name, stock, price)
VALUES ('0003', '103', 'Penguin Stuffie', 10, 14.99);

INSERT
INTO Items(i_id, p_id, name, stock, price)
VALUES ('0004', '104', 'Bear Balloon', 30, 4.99);

INSERT
INTO Items(i_id, p_id, name, stock, price)
VALUES ('0005', '105', 'Hamburger', 40, 6.99);

INSERT
INTO Animals2(species, genus)
VALUES ('Tiger', 'panthera');

INSERT
INTO Animals2(species, genus)
VALUES ('Lion', 'panthera');

INSERT
INTO Animals2(species, genus)
VALUES ('Emperor Penguin', 'Pinguinus');

INSERT
INTO Animals2(species, genus)
VALUES ('Northern Giraffe', 'Giraffa');

INSERT
INTO Animals2(species, genus)
VALUES ('Grizzly Bear', 'Ursus');

INSERT
INTO Animals1(a_id, p_id, name, species)
VALUES ('1001', '001', 'Stripe', 'Tiger');

INSERT
INTO Animals1(a_id, p_id, name, species)
VALUES ('1002', '002', 'Fluffy', 'Lion');

INSERT
INTO Animals1(a_id, p_id, name, species)
VALUES ('1003', '003', 'Slippy', 'Emperor Penguin');

INSERT
INTO Animals1(a_id, p_id, name, species)
VALUES ('1004', '004', 'Spots', 'Northern Giraffe');

INSERT
INTO Animals1(a_id, p_id, name, species)
VALUES ('1005', '003', 'Cuddles', 'Grizzly Bear');

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1001', 'Deer meat', 20.1);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1002', 'Raw beef', 10.0);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1003', 'Mashed sardines', 15.7);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1004', 'Prepared hay', 25.0);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1005', 'Cooked moose', 30.2);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1001', 'Raw beef', 20.1);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1002', 'Deer meat', 10.0);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1003', 'Tuna', 15.7);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1004', 'Vegetables', 25.0);

INSERT
INTO Prepped_Food(a_id, name, weight)
VALUES ('1005', 'Cooked elk', 30.2);

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES('1', 'Deer and Moose meat', 50, TO_DATE('2023/01/27', 'yyyy/mm/dd'), TO_DATE('2023/02/05',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES('2', 'Sardines', 30, TO_DATE('2023/01/15',  'yyyy/mm/dd'), TO_DATE('2023/02/01',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES ('3', 'Hay and fruits', 32, TO_DATE('2023/01/15',  'yyyy/mm/dd'), TO_DATE('2023/02/10',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES ('4', 'Raw beef and chicken', 10, TO_DATE('2023/02/25',  'yyyy/mm/dd'), TO_DATE('2023/03/09',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES ('5', 'Pellet food for Giraffes', 100, TO_DATE('2023/01/08',  'yyyy/mm/dd'), TO_DATE('2023/06/08',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES('6', 'Deer and Moose meat', 12, TO_DATE('2023/01/27', 'yyyy/mm/dd'), TO_DATE('2023/02/05',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES('7', 'Sardines', 8, TO_DATE('2023/01/15',  'yyyy/mm/dd'), TO_DATE('2023/02/01',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES ('8', 'Hay and fruits', 10, TO_DATE('2023/01/15',  'yyyy/mm/dd'), TO_DATE('2023/02/10',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES ('9', 'Raw beef and chicken', 10, TO_DATE('2023/02/25',  'yyyy/mm/dd'), TO_DATE('2023/03/09',  'yyyy/mm/dd'));

INSERT
INTO Raw_Food_Orders(o_id, contents, weight, date_received, expiry_date)
VALUES ('10', 'Pellet food for Giraffes', 100, TO_DATE('2023/01/08',  'yyyy/mm/dd'), TO_DATE('2023/06/08',  'yyyy/mm/dd'));

INSERT
INTO Works_at(w_id, p_id)
VALUES ('1', '101');

INSERT
INTO Works_at(w_id, p_id)
VALUES ('2', '102');

INSERT
INTO Works_at(w_id, p_id)
VALUES ('3', '103');

INSERT
INTO Works_at(w_id, p_id)
VALUES ('4', '104');

INSERT
INTO Works_at(w_id, p_id)
VALUES ('5', '105');

INSERT
INTO Assigned_to(w_id, p_id)
VALUES ('1', '001');

INSERT
INTO Assigned_to(w_id, p_id)
VALUES ('2', '002');

INSERT
INTO Assigned_to(w_id, p_id)
VALUES ('3', '003');

INSERT
INTO Assigned_to(w_id, p_id)
VALUES ('4', '004');

INSERT
INTO Assigned_to(w_id, p_id)
VALUES ('5', '005');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('1', '1001');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('2', '1002');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('3', '1003');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('4', '1004');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('5', '1005');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('6', '1001');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('6', '1002');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('6', '1003');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('6', '1004');

INSERT
INTO Feeds(w_id, a_id)
VALUES ('6', '1005');


INSERT
INTO Maintains_Health_of(w_id, a_id)
VALUES ('1', '1001');

INSERT
INTO Maintains_Health_of(w_id, a_id)
VALUES ('2', '1002');

INSERT
INTO Maintains_Health_of(w_id, a_id)
VALUES ('3', '1003');

INSERT
INTO Maintains_Health_of(w_id, a_id)
VALUES ('4', '1004');

INSERT
INTO Maintains_Health_of(w_id, a_id)
VALUES ('5', '1005');

INSERT
INTO Cohabitates_with(a_id1, a_id2)
VALUES ('1001', '1002');

INSERT
INTO Cohabitates_with(a_id1, a_id2)
VALUES ('1001', '1003');

INSERT
INTO Cohabitates_with(a_id1, a_id2)
VALUES ('1001', '1004');

INSERT
INTO Cohabitates_with(a_id1, a_id2)
VALUES ('1001', '1005');

INSERT
INTO Cohabitates_with(a_id1, a_id2)
VALUES ('1003', '1004');

INSERT
INTO Made_from(a_id, name, o_id)
VALUES ('1001', 'Deer meat', '1');

INSERT
INTO Made_from(a_id, name, o_id)
VALUES ('1002', 'Raw beef', '4');

INSERT
INTO Made_from(a_id, name, o_id)
VALUES ('1003', 'Mashed sardines', '2');

INSERT
INTO Made_from(a_id, name, o_id)
VALUES ('1004', 'Prepared hay', '3');

INSERT
INTO Made_from(a_id, name, o_id)
VALUES ('1005', 'Cooked moose', '1');

INSERT
INTO Stored_at(a_id, name, p_id)
VALUES ('1001', 'Deer meat', '201');

INSERT
INTO Stored_at(a_id, name, p_id)
VALUES ('1002', 'Raw beef', '201');

INSERT
INTO Stored_at(a_id, name, p_id)
VALUES ('1003', 'Mashed sardines', '201');

INSERT
INTO Stored_at(a_id, name, p_id)
VALUES ('1004', 'Prepared hay', '201');

INSERT
INTO Stored_at(a_id, name, p_id)
VALUES ('1005', 'Cooked moose', '201');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('1', '201');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('2', '205');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('3', '203');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('4', '201');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('5', '204');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('6', '201');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('7', '205');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('8', '203');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('9', '201');

INSERT
INTO Located_at(o_id, p_id)
VALUES ('10', '204');

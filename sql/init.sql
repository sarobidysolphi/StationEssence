CREATE DATABASE IF NOT EXISTS station_essence;
USE station_essence;

CREATE TABLE IF NOT EXISTS PRODUIT (
    numProd VARCHAR(10) PRIMARY KEY,
    Design VARCHAR(100) NOT NULL,
    stock INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ENTREE (
    numEntree VARCHAR(10) PRIMARY KEY,
    numProd VARCHAR(10) NOT NULL,
    stockEntree INT NOT NULL,
    dateEntree DATE NOT NULL,
    FOREIGN KEY (numProd) REFERENCES PRODUIT(numProd)
);

CREATE TABLE IF NOT EXISTS ACHAT (
    numAchat VARCHAR(10) PRIMARY KEY,
    numProd VARCHAR(10) NOT NULL,
    nomClient VARCHAR(100) NOT NULL,
    nbrLitre INT NOT NULL,
    dateAchat DATE NOT NULL,
    FOREIGN KEY (numProd) REFERENCES PRODUIT(numProd)
);

CREATE TABLE IF NOT EXISTS SERVICE (
    numServ VARCHAR(10) PRIMARY KEY,
    service VARCHAR(100) NOT NULL,
    prix INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ENTRETIEN (
    numEntr VARCHAR(10) PRIMARY KEY,
    numServ VARCHAR(10) NOT NULL,
    Immatriculation_voiture VARCHAR(20) NOT NULL,
    nomClient VARCHAR(100) NOT NULL,
    dateEntretien DATE NOT NULL,
    FOREIGN KEY (numServ) REFERENCES SERVICE(numServ)
);

INSERT IGNORE INTO PRODUIT (numProd, Design, stock) VALUES
('P001', 'Essence', 530),
('P002', 'Gasoil', 245),
('P003', 'Petrole', 90);

INSERT IGNORE INTO SERVICE (numServ, service, prix) VALUES
('S001', 'Lavage', 20000),
('S002', 'Gonflage', 2000),
('S003', 'Vidange', 35000),
('S004', 'Graissage', 10000);

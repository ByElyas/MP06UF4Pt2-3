/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Vehicle;
import model.Conductor;

/**
 *
 * @author eliesfatsini
 */
public class Model {

    private String urlBD;
    private String userBD;
    private String passwordUserBD;
    private String bdDriver;

    public void carregaVariables() throws FileNotFoundException, IOException {
        String fitxerPr = "bd.properties";
        Properties props = new Properties();

        props.load(new FileInputStream(fitxerPr));

        userBD = props.getProperty("user");
        passwordUserBD = props.getProperty("passwordUser");
        urlBD = props.getProperty("url");
        bdDriver = props.getProperty("driver");
    }

    public Model() {
        try {
            carregaVariables();
        } catch (Exception ex) {
            System.out.println("Algo no ha anat bè al carregar les variables de la db");
        }

        try {
            crearEsquemaBD();
        } catch (SQLSyntaxErrorException ex) {
            System.out.println("Error de sintaxi a la creació de les taules. Contactar el programador");           
        } catch (SQLException exd) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, exd);
        }
    }

    /**
     *
     * @return
     */
    
    
    // CREACIÓ DE LES TAULES A LA BD AMB AQUEST METODE QUE S'EXECUTA AL EXECUTARSE EL Model
    // El "DROP TABLE IF EXIST" està comentat ja que si no cada vegada que iniciem el programa es borra tot el que hi ha
    // en aquestes taules a la bd
    public void crearEsquemaBD() throws SQLException {
        Statement sta = this.getConnection().createStatement();
        
//        sta.executeUpdate("SET FOREIGN_KEY_CHECKS=0;");
//        sta.executeUpdate("DROP TABLE IF EXISTS `vehicle`;");
//        sta.executeUpdate("SET FOREIGN_KEY_CHECKS=1;");
        sta.executeUpdate("CREATE TABLE IF NOT EXISTS `vehicle` (\n"
                + "  `_1_numero_Vehicle` int NOT NULL,\n"
                + "  `_2_model_Vehicle` text NOT NULL,\n"
                + "  `_3_any_Vehicle` int NOT NULL,\n"
                + "  `_4_marca_Vehicle` text NOT NULL,\n"
                + "  PRIMARY KEY (`_1_numero_Vehicle`)\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");

//        sta.executeUpdate("DROP TABLE IF EXISTS `conductor`;");
        sta.executeUpdate("CREATE TABLE IF NOT EXISTS `conductor` (\n"
                + "  `_1_id_conductor` int NOT NULL,\n"
                + "  `_2_cognom_Conductor` text NOT NULL,\n"
                + "  `_3_edat_Conductor` int NOT NULL,\n"
                + "  `_4_nom_Conductor` text NOT NULL,\n"
                + "  `_5_vehicle_Conductor` int NOT NULL,\n"
                + "  PRIMARY KEY (`_1_id_conductor`),\n"
                + "  KEY `fk_conductor_vehicle` (`_5_vehicle_Conductor`),\n"
                + "  CONSTRAINT `fk_conductor_vehicle` FOREIGN KEY (`_5_vehicle_Conductor`) REFERENCES `vehicle` (`_1_numero_Vehicle`)\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");

    }

    public Connection getConnection() throws SQLException {
        Connection con;
        con = DriverManager.getConnection(urlBD, userBD, passwordUserBD);
        return con;

    }

    public void closeConnection() throws SQLException {
        Connection con = this.getConnection();
        con.close();
    }

    //Vehciles
    private Collection<Vehicle> data = new TreeSet<>();
    private Collection<Vehicle> dataOrd = new TreeSet<>(new VehicleOrdenatMarca());

    public Collection<Vehicle> getData() {
        return data;
    }

    public Collection<Vehicle> getDataOrd() {
        return dataOrd;
    }

    //metode generic per a insertar dades --- tambè serveix per a conductor
    public static <T> void insertar(T a, Collection<T> col) {
        col.add(a);
    }

    public void poblarTaula() {
        try {
            //COSA DE BASE DE DADES OLEEE
            this.buidarCol();

            //COSA DE VEHICLE
            Statement sta = this.getConnection().createStatement();
            ResultSet result = sta.executeQuery("SELECT * FROM vehicle;");

            int numV;
            String modelV;
            int anyV;
            String marcaV;

            while (result.next()) {
                numV = result.getInt("_1_numero_Vehicle");
                modelV = result.getString("_2_model_Vehicle");
                anyV = result.getInt("_3_any_Vehicle");
                marcaV = result.getString("_4_marca_Vehicle");
                this.insertarVehicle(marcaV, modelV, anyV, numV);
            }

            //COSA DE CONDUCTOR
            Statement stac = this.getConnection().createStatement();
            ResultSet resultc = stac.executeQuery("SELECT * FROM conductor;");

            int idC;
            String cognomC;
            int edatC;
            String nomC;
            int vehicleC;

            while (resultc.next()) {
                idC = resultc.getInt("_1_id_conductor");
                cognomC = resultc.getString("_2_cognom_Conductor");
                edatC = resultc.getInt("_3_edat_Conductor");
                nomC = resultc.getString("_4_nom_Conductor");
                vehicleC = resultc.getInt("_5_vehicle_Conductor");
                this.insertarConductor(nomC, cognomC, edatC, idC, vehicleC);
            }
        } catch (SQLException ex) {

        }
    }

    public void insertarVehicle(String marca, String model, int any, int numero) {
        Vehicle ve = new Vehicle(marca, model, any, numero);
        Model.insertar(ve, data);
        Model.insertar(ve, dataOrd);
//        Connection conV = DriverManager.getConnection(urlBD, userBD, passwordUserBD);
//        String query = " INSERT INTO vehicle VALUES (?, ?, ?, ?) ";
//        
//        PreparedStatement preparedStmt = conV.prepareStatement(query);
//        preparedStmt.setInt(1, numero);
//        preparedStmt.setString(2, model);
//        preparedStmt.setInt(3, any);
//        preparedStmt.setString(4, marca);
//        
//        preparedStmt.execute();
//        
//        conV.close();
    }

    public void insertarVehicleBD(String marca, String model, int any, int numero) throws SQLException {

//            Vehicle ve = new Vehicle(marca, model, any, numero);
//        Model.insertar(ve, data);
//        Model.insertar(ve, dataOrd);
        Connection con = this.getConnection();
        String query = " INSERT INTO vehicle VALUES (?, ?, ?, ?) ";

        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setInt(1, numero);
        preparedStmt.setString(2, model);
        preparedStmt.setInt(3, any);
        preparedStmt.setString(4, marca);

        preparedStmt.execute();

    }

    public void editarVehicleBD(String marca, String model, int any, int numero) {
        try {
            String query = " UPDATE vehicle SET _2_model_Vehicle = ?, _3_any_Vehicle = ?, _4_marca_Vehicle = ? WHERE _1_numero_Vehicle = ?";

            Connection con = this.getConnection();
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(4, numero);
            preparedStmt.setString(1, model);
            preparedStmt.setInt(2, any);
            preparedStmt.setString(3, marca);

            preparedStmt.execute();

        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //metode generic per a eliminar dades 
    public static <T> void eliminar(T a, Collection<T> col) {
        col.remove(a);
    }

    public void buidarCol() {
        data.clear();
        dataOrd.clear();
        dataConductor.clear();
        dataOrdConductor.clear();
    }

    public void eliminarVehicleBD(Vehicle v) {
        try {
            Connection con = this.getConnection();
            String query = " DELETE FROM vehicle WHERE _1_numero_Vehicle = ?";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, v.get1_numero_Vehicle());

            preparedStmt.execute();

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public void actualitzarVehicle(String marca, String model, int any, int numero) {
//
//    }
    class VehicleOrdenatMarca implements Comparator<Vehicle> {

        @Override
        public int compare(Vehicle o1, Vehicle o2) {
//            return o1.get4_marca_Vehicle().compareTo(o2.get4_marca_Vehicle());
            int p;
            p = o1.get4_marca_Vehicle().compareTo(o2.get4_marca_Vehicle());
            if (p != 0) {
                return p;
            }
            return o1.get2_model_Vehicle().compareTo(o2.get2_model_Vehicle());
        }
    }

    //Conductor
    private Collection<Conductor> dataConductor = new TreeSet<>();
    private Collection<Conductor> dataOrdConductor = new TreeSet<>(new ConductorOrdenatNom());

    public Collection<Conductor> getDataConductor() {
        return dataConductor;
    }

    public Collection<Conductor> getDataOrdConductor() {
        return dataOrdConductor;
    }

    public void insertarConductor(String nom, String cognom, int edat, int id, int vehicle_Conductor) {
        Conductor co = new Conductor(nom, cognom, edat, id, vehicle_Conductor);
        Model.insertar(co, dataConductor);
        Model.insertar(co, dataOrdConductor);

    }

    public void insertarConductorBD(String nom, String cognom, int edat, int id, int vehicle_Conductor) throws SQLException {
        Connection con = this.getConnection();
        String query = " INSERT INTO conductor VALUES (?, ?, ?, ?, ?) ";

        PreparedStatement preparedStmt;

        preparedStmt = con.prepareStatement(query);

        preparedStmt.setInt(1, id);
        preparedStmt.setString(2, cognom);
        preparedStmt.setInt(3, edat);
        preparedStmt.setString(4, nom);
        preparedStmt.setInt(5, vehicle_Conductor);

        preparedStmt.execute();
    }

    public void eliminarConductorBD(Conductor c) {
//        Model.eliminar(algo, dataConductor);
//        Model.eliminar(algo, dataOrdConductor);

        try {
            Connection con = this.getConnection();
            String query = " DELETE FROM conductor WHERE _1_id_conductor = ?";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, c.get1_id_Conductor());

            preparedStmt.execute();

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editarConductorBD(String nom, String cognom, int edat) {
        try {
            String query = " UPDATE conductor SET _2_cognom_Conductor = ?, _3_edat_Conductor = ?, _4_nom_Conductor = ? WHERE _1_id_Conductor = ?";

            Connection con = this.getConnection();
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, cognom);
            preparedStmt.setInt(2, edat);
            preparedStmt.setString(3, nom);

            preparedStmt.execute();

        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class ConductorOrdenatNom implements Comparator<Conductor> {

        @Override
        public int compare(Conductor o1, Conductor o2) {
            // return o1.get4_nom_Conductor().compareTo(o2.get4_nom_Conductor());
            int p;
            p = o1.get4_nom_Conductor().compareTo(o2.get4_nom_Conductor());
            if (p != 0) {
                return p;
            }
            return o1.get2_cognom_Conductor().compareTo(o2.get2_cognom_Conductor());
        }

    }

}

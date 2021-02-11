/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import elsmeusbeans.*;
import java.beans.PropertyVetoException;
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
//
//    private String urlBD;
//    private String userBD;
//    private String passwordUserBD;
//    private String bdDriver;

    Pr2i3 p = new Pr2i3();

//    public void carregaVariables() throws FileNotFoundException, IOException {
//        String fitxerPr = "bd.properties";
//        Properties props = new Properties();
//
//        props.load(new FileInputStream(fitxerPr));
//
//        userBD = props.getProperty("user");
//        passwordUserBD = props.getProperty("passwordUser");
//        urlBD = props.getProperty("url");
//        bdDriver = props.getProperty("driver");
//    }
    public Model() {
        try {
            //CONNECTAR EL BEAN A LA BD
            p.setPropsDB("bd.properties");

            //CREAR LES TAULES PER DEFECTE A LA BD
            p.setQuery_db("CREATE TABLE IF NOT EXISTS `vehicle` (\n"
                    + "  `_1_numero_Vehicle` int NOT NULL,\n"
                    + "  `_2_model_Vehicle` text NOT NULL,\n"
                    + "  `_3_any_Vehicle` int NOT NULL,\n"
                    + "  `_4_marca_Vehicle` text NOT NULL,\n"
                    + "  PRIMARY KEY (`_1_numero_Vehicle`)\n"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
            p.setQuery_db("CREATE TABLE IF NOT EXISTS `conductor` (\n"
                    + "  `_1_id_conductor` int NOT NULL,\n"
                    + "  `_2_cognom_Conductor` text NOT NULL,\n"
                    + "  `_3_edat_Conductor` int NOT NULL,\n"
                    + "  `_4_nom_Conductor` text NOT NULL,\n"
                    + "  `_5_vehicle_Conductor` int NOT NULL,\n"
                    + "  PRIMARY KEY (`_1_id_conductor`),\n"
                    + "  KEY `fk_conductor_vehicle` (`_5_vehicle_Conductor`),\n"
                    + "  CONSTRAINT `fk_conductor_vehicle` FOREIGN KEY (`_5_vehicle_Conductor`) REFERENCES `vehicle` (`_1_numero_Vehicle`)\n"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @return
     */
//    public Connection getConnection() throws SQLException {
////        Pr2i3 p = new Pr2i3();          
////
////        try {
////            p.setPropsDB("bd.properties");
////        } catch (PropertyVetoException ex) {
////            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
////        }     
//     
//    }
//
//    public void closeConnection() throws SQLException {
//        Connection con = this.getConnection();
//        con.close();
//    }
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
//            Statement sta = this.getConnection().createStatement();            
//            ResultSet result = sta.executeQuery();
            try {
                p.setQuery_db("SELECT * FROM vehicle;");
            } catch (PropertyVetoException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }

            int numV;
            String modelV;
            int anyV;
            String marcaV;

            ResultSet result = p.getRst();

            while (result.next()) {
                numV = result.getInt("_1_numero_Vehicle");
                modelV = result.getString("_2_model_Vehicle");
                anyV = result.getInt("_3_any_Vehicle");
                marcaV = result.getString("_4_marca_Vehicle");
                this.insertarVehicle(marcaV, modelV, anyV, numV);
            }

            //COSA DE CONDUCTOR
            try {
                p.setQuery_db("SELECT * FROM conductor;");
            } catch (PropertyVetoException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }

            int idC;
            String cognomC;
            int edatC;
            String nomC;
            int vehicleC;

            ResultSet resultc = p.getRst();

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

    }

    public void insertarVehicleBD(String marca, String model, int any, int numero) throws SQLException {

        try {
            System.out.println("INSERT INTO vehicle VALUES (" + numero + ",'" + model + "'," + any + ",'" + marca + "');");
            p.setUpdate_db("INSERT INTO vehicle VALUES (" + numero + ",'" + model + "'," + any + ",'" + marca + "') ");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void insertarConductor(String nom, String cognom, int edat, int id, int vehicle_Conductor) {
        Conductor co = new Conductor(nom, cognom, edat, id, vehicle_Conductor);
        Model.insertar(co, dataConductor);
        Model.insertar(co, dataOrdConductor);

    }

    public void editarVehicleBD(String marca, String model, int any, int numero) {


        try {
            p.setUpdate_db("UPDATE vehicle SET _2_model_Vehicle = '"+model+"', _3_any_Vehicle = "+any+", _4_marca_Vehicle = '"+marca+"' WHERE _1_numero_Vehicle = "+numero+";");
        } catch (PropertyVetoException ex) {
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

            p.setUpdate_db("DELETE FROM vehicle WHERE _1_numero_Vehicle = " + v.get1_numero_Vehicle() + ";");
        } catch (PropertyVetoException ex) {
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

    public void insertarConductorBD(String nom, String cognom, int edat, int id, int vehicle_Conductor) throws SQLException {
        try {

            p.setUpdate_db("INSERT INTO conductor VALUES (" + id + ",'" + cognom + "'," + edat + ",'" + nom + "'," + vehicle_Conductor + "); ");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eliminarConductorBD(Conductor c) {
        try {
            p.setUpdate_db("DELETE FROM conductor WHERE _1_id_conductor = " + c.get1_id_Conductor() + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editarConductorBD(String nom, String cognom, int edat, int id) {
        try {
            p.setUpdate_db("UPDATE conductor SET _2_cognom_Conductor = '" + cognom + "', _3_edat_Conductor = " + edat + ", _4_nom_Conductor = '" + nom + "' WHERE _1_id_Conductor = " + id + "");
        } catch (PropertyVetoException ex) {
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

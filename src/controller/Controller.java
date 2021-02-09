/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import elsmeusbeans.Pr2i3;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import model.Conductor;
import model.Model;
import model.Vehicle;
import utilscontroller.Utils;
import view.View;
import java.sql.*;
import java.util.Properties;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author SrBetllies
 */
public class Controller {

    private static Model modelo;
    private static View view;

//    private int comboboxActualCond = 0;
    private int colVehicleActual = 0;
    private int colConductorActual = 0;
    private int filaSel = -1;
    private int filaSelCond = -1;
    private TableColumn tc;
    private TableColumn tcC;

    public Controller(Model m, View v) throws SQLException, FileNotFoundException, IOException {

        view = v;
        modelo = m;

        //COSA DE CARREGAR COSES DEL JAVABEAN ESTE, QUE ANTES ESTAVEN AL MODEL
        Pr2i3 p = new Pr2i3();
        try {
            p.setPropsDB("bd.properties");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
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
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        controlador();

    }

    private void defecteText() {
        /**
         * VEHICLE
         */

        ///////////////////////////
        //TITOL PANEL VEHICLE
        view.getVehicleLabel().setText("VEHICLES");

        //CAIXES DE TEXT - FORMULARI
//        view.getAfegirMarcaText().setSize(0, 0);
        view.getAfegirMarcaText().setText("Marca Placeholder");
        view.getAfegirModelText().setText("Model Placeholder");
        view.getAfegirAnyText().setText("1900");
        view.getAfegirNumeroText().setText("99");

        //FILTRE
        view.getFiltrarVehiclesCombobox().removeAllItems();
        view.getFiltrarVehiclesCombobox().addItem("Ordenar per numero");
        view.getFiltrarVehiclesCombobox().addItem("Ordenar per marca");

        //LABELS - FORMULARI
        view.getAfegirMarcaLabel().setText("Marca Vehicle");
        view.getAfegirModelLabel().setText("Model Vehicle");
        view.getAfegirAnyLabel().setText("Any Vehicle");
        view.getAfegirNumeroLabel().setText("Numero Vehicle");

        //BOTÓ SUBMIT - FORMULARI
        view.getAfegirVehicleButton().setText("Afegir registre");

        //FORMULARI ELIMINAR - TEXT DEFECTE
        view.getEliminarVehicleButton().setText("Eliminar fila!");

        //FORMULARI -EDITAR
        view.getEditarVehicleButton().setText("Editar!");
        view.getEditarAnyLabel().setText("Any Vehicle");
        view.getEditarAnyText().setText("9999");
        view.getEditarMarcaLabel().setText("Marca Vehicle");
        view.getEditarMarcaText().setText("Placeholder");
        view.getEditarModelLabel().setText("Model Vehicle");
        view.getEditarModelText().setText("Placeholder");
        view.getEditarNumeroLabel().setText("Numero Vehicle");
        view.getEditarNumeroText().setText("999");
//        tcmE.removeColumn(tc);

        /**
         * Conductor
         */
        //TITOL PANEL VEHICLE
        view.getConductorLabel().setText("CONDUCTORS");

        //CAIXES DE TEXT - FORMULARI
//        view.getAfegirMarcaText().setSize(0, 0);
        view.getAfegirNomConductorText().setText("Nom Placeholder");
        view.getAfegirCognomConductorText().setText("Cognom Placeholder");
        view.getAfegirEdatConductorText().setText("999");
        view.getAfegirIdConductorText().setText("99999");

        //FILTRE
        view.getFiltrarConductorCombobox().removeAllItems();
        view.getFiltrarConductorCombobox().addItem("Ordenar per ID");
        view.getFiltrarConductorCombobox().addItem("Ordenar per nom");

        //LABELS - FORMULARI
        view.getAfegirNomConductorLabel().setText("Nom Conductor");
        view.getAfegirCognomConductorLabel().setText("Cognom Conductor");
        view.getAfegirEdatConductorLabel().setText("Edat Conductor");
        view.getAfegirIdConductorLabel().setText("ID Conductor");

        //BOTÓ SUBMIT - FORMULARI
        view.getAfegirConductorButton().setText("Afegir registre");

        //FORMULARI ELIMINAR - TEXT DEFECTE
        view.getEliminarConductorButton().setText("Eliminar fila!");

        view.getEditarConductorButton().setText("Editar!");
        view.getEditarNomConductorLabel().setText("Nom Conductor");
        view.getEditarEdatConductorText().setText("18");
        view.getEditarCognomConductorLabel().setText("Cognom Conductor");
        view.getEditarNomConductorText().setText("Placeholder");
        view.getEditarCognomConductorText().setText("Placeholder");
        view.getEditarEdatConductorLabel().setText("Edat Conductor");
        view.getEditarIdConductorLabel().setText("ID Conductor");
        view.getEditarIdConductorText().setText("99999");
        view.getNumVehicleConductorLabel().setText("Numero del vehicle");

        actualitzarComboboxCond();
    }

    public void actualitzarComboboxCond() {
        //Combobox per a elegir vehicle per als conductors nous               
        view.getNumVehicleConductorCombobox().removeAllItems();
//        Combobox per a elegir vehicle per al conductor 
        Utils.<Vehicle>loadCombo(modelo.getData(), view.getNumVehicleConductorCombobox());
    }

    public void carregarTaulaVehicle() {
        tc = Utils.<Vehicle>loadTable(modelo.getData(), view.getJTaulaVehicles(), Vehicle.class, true);

    }

    public void carregarTaulaVehicleOrdenada() {
//        modelo.getDataOrd().addAll(modelo.getData());
//        tc = Utils.<Vehicle>loadTable(modelo.getDataOrd(), view.getJTaulaVehicles(), Vehicle.class, true, true);
        tc = Utils.<Vehicle>loadTable(modelo.getDataOrd(), view.getJTaulaVehicles(), Vehicle.class, true);
    }

    public void carregarTaulaVehicleActual() {
        if (colVehicleActual == 0) {
            carregarTaulaVehicle();
        } else {
            carregarTaulaVehicleOrdenada();
        }
    }

    public void carregarTaulaConductor() {
//        modelo.getDataConductor().addAll(modelo.getDataOrdConductor());
        tcC = Utils.<Conductor>loadTable(modelo.getDataConductor(), view.getJTaulaConductor(), Conductor.class, true);
    }

    public void carregarTaulaConductorOrdenada() {
//        modelo.getDataOrdConductor().addAll(modelo.getDataConductor());
        tcC = Utils.<Conductor>loadTable(modelo.getDataOrdConductor(), view.getJTaulaConductor(), Conductor.class, true);
    }

    public void carregarTaulaConductorActual() {

        if (colConductorActual == 0) {
            carregarTaulaConductor();
        } else {
            carregarTaulaConductorOrdenada();
        }
    }

    private void carregarBD() {

        modelo.poblarTaula();
        carregarTaulaVehicleActual();
        carregarTaulaConductorActual();

    }

    private void controlador() {

        carregarBD();

        //Codi que inicilitza la vista
        view.setVisible(true);
        //Inicialitzem els textos per defecte que ens mostrarà l'alicatiu
        defecteText();

        //TAULA
        //Carregar les dades localitzades a Model.java, tambè servirà per al insertar dades      
        carregarTaulaVehicleActual();
        carregarTaulaConductorActual();

        //ACCIONS DE CRUD AQUI
        //VEHICLE
        //Afegir text dinamic a l'apartat de edit
        view.getJTaulaVehicles().addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                filaSel = view.getJTaulaVehicles().getSelectedRow();
                TableColumnModel tcmMC = view.getJTaulaVehicles().getColumnModel();
                tcmMC.addColumn(tc);
                Vehicle vehE = (Vehicle) view.getJTaulaVehicles().getValueAt(filaSel, tcmMC.getColumnCount() - 1);
                view.getEditarNumeroText().setText(String.valueOf(vehE.get1_numero_Vehicle()));
                view.getEditarAnyText().setText(String.valueOf(vehE.get3_any_Vehicle()));
                view.getEditarModelText().setText(vehE.get2_model_Vehicle());
                view.getEditarMarcaText().setText(vehE.get4_marca_Vehicle());
                TableColumnModel tcmC2 = view.getJTaulaConductor().getColumnModel();
                try {
                    if (filaSel != -1) {
                        ListSelectionModel selmodC = view.getJTaulaConductor().getSelectionModel();
                        selmodC.clearSelection();

                        for (int i = 0; i < tcmC2.getColumnCount(); i++) {
                            int v = ((Integer) view.getJTaulaConductor().getValueAt(i, tcmC2.getColumnCount() - 1));
                            if (v == vehE.get1_numero_Vehicle()) {
                                selmodC.addSelectionInterval(i, i);
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException out) {

                }
                tcmMC.removeColumn(tc);
            }
        }
        );

        // DES-SEL·LECCIONAR FILES SI CLICKEM A EL JPANEL
        view.getjPanel1().addMouseListener(
                new MouseAdapter() {
            public void mouseClicked(MouseEvent a) {
                ListSelectionModel selmodC = view.getJTaulaConductor().getSelectionModel();
                selmodC.clearSelection();

                ListSelectionModel selmodV = view.getJTaulaVehicles().getSelectionModel();
                selmodV.clearSelection();
                carregarBD();
            }
        }
        );

        view.getjPanel2().addMouseListener(
                new MouseAdapter() {
            public void mouseClicked(MouseEvent a) {
                ListSelectionModel selmodC = view.getJTaulaConductor().getSelectionModel();
                selmodC.clearSelection();

                ListSelectionModel selmodV = view.getJTaulaVehicles().getSelectionModel();
                selmodV.clearSelection();
                carregarBD();
            }
        }
        );

        //EDITAR VEHICLE
        view.getEditarVehicleButton().addActionListener(e -> {
            if (filaSel != -1) {
                TableColumnModel tcm = view.getJTaulaVehicles().getColumnModel();
                tcm.addColumn(tc);
                Vehicle veh = (Vehicle) view.getJTaulaVehicles().getValueAt(filaSel, tcm.getColumnCount() - 1);
                int num = Integer.parseInt(view.getEditarNumeroText().getText());
                String model = view.getEditarModelText().getText();
                int any = Integer.parseInt(view.getEditarAnyText().getText());
                String marca = view.getEditarMarcaText().getText();
                tcm.removeColumn(tc);
                modelo.editarVehicleBD(marca, model, any, num);
                filaSel = -1;
                carregarBD();
            } else {
                System.out.println(filaSel);
                JOptionPane.showMessageDialog(view, "Has de seleccionar una fila per a editarla");
            }
        }
        );

        //afegirVehicle
        view.getAfegirVehicleButton().addActionListener(e -> {

            if (view.getAfegirNumeroText().getText().isBlank()
                    || view.getAfegirMarcaText().getText().isBlank()
                    || view.getAfegirModelText().getText().isBlank()
                    || view.getAfegirAnyText().getText().isBlank()) {
                JOptionPane.showMessageDialog(view, "Hi ha algun camp buit. No pot haver-hi cap camp buit!");
            } else {
                //Exemple de validesa utilitzant expressions regulars
                if (view.getAfegirNumeroText().getText().matches("\\d{2}") || view.getAfegirNumeroText().getText().matches("\\d{1}")) {

                    try {
                        if (Integer.parseInt(view.getAfegirAnyText().getText()) < 1900
                                || Integer.parseInt(view.getAfegirAnyText().getText()) > 2030) {
                            JOptionPane.showMessageDialog(view, "El any ha de ser valid (entre 1900 i 2030)");
                        } else {
                            modelo.insertarVehicleBD(view.getAfegirMarcaText().getText(),
                                    view.getAfegirModelText().getText(),
                                    Integer.parseInt(view.getAfegirAnyText().getText()),
                                    Integer.parseInt(view.getAfegirNumeroText().getText())
                            );
                            carregarBD();
                        }
                    } catch (NumberFormatException x) {
                        JOptionPane.showMessageDialog(view, "El any ha de ser un ANY (en numeros, no escrit)");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(view, "Error SQL: No pot haver-hi dos registres amb la mateixa ID!");
                    }
                    actualitzarComboboxCond();
                    carregarTaulaVehicleActual();
                    carregarTaulaVehicleActual();
                } else {
                    JOptionPane.showMessageDialog(view, "El numero del vehicle ha de ser inferior a 100! I no pot ser"
                            + " una paraula, lletres, etc..");
                }
            }

        }
        );

        //eliminarVehicle
        view.getEliminarVehicleButton().addActionListener(e -> {
            if (filaSel != -1) {
                Connection conE = null;
                try {
                    conE = modelo.getConnection();
                } catch (SQLException ex) {
                }
                try {

                    conE.setAutoCommit(false);
                    TableColumnModel tcm = view.getJTaulaVehicles().getColumnModel();
                    tcm.addColumn(tc);
                    Vehicle veh = (Vehicle) view.getJTaulaVehicles().getValueAt(filaSel, tcm.getColumnCount() - 1);
                    tcm.removeColumn(tc);
                    for (int i = 0; i < view.getJTaulaConductor().getRowCount(); i++) {
                        TableColumnModel tcmA = view.getJTaulaConductor().getColumnModel();
                        tcmA.addColumn(tcC);
                        Conductor cond = (Conductor) view.getJTaulaConductor().getValueAt(i, tcmA.getColumnCount() - 1);
                        tcmA.removeColumn(tcC);
                        if (cond.get5_vehicle_Conductor() == veh.get1_numero_Vehicle()) {
                            modelo.eliminarConductorBD(cond);
                            carregarTaulaVehicleActual();
                            carregarTaulaConductorActual();
                        }
                    }
                    modelo.eliminarVehicleBD(veh);
                    conE.commit();
                    actualitzarComboboxCond();
                    carregarBD();
                    carregarTaulaVehicleActual();
                    carregarTaulaConductorActual();

                    filaSel = -1;
                } catch (SQLException ex) {
                    System.out.println("Algo no ha anat bè al borrar registres. Tirant atras el commit...");
                    try {
                        conE.rollback();
                    } catch (SQLException ex1) {
                        System.out.println("Ha fallat el rollback, S'ACABA EL MON CORREU NOIS CORREU");
                    }
                }
            } else {
                System.out.println(filaSel);
                JOptionPane.showMessageDialog(view, "Has de seleccionar una fila per a borrarla!");
            }
            carregarBD();
        }
        );

        //FILTRE 
        view.getFiltrarVehiclesCombobox().addItemListener(e -> {
            if (view.getFiltrarVehiclesCombobox().getSelectedIndex() == 0) {
                colVehicleActual = 0;
                carregarTaulaVehicleActual();
            }
            if (view.getFiltrarVehiclesCombobox().getSelectedIndex() == 1) {
                colVehicleActual = 1;
                carregarTaulaVehicleActual();
            }
        }
        );

        ////
        //CONDUCTOR
        ////
        //editarVehicle
        //Afegir text dinamic a l'apartat de edit
        view.getJTaulaConductor().addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                filaSelCond = view.getJTaulaConductor().getSelectedRow();
                TableColumnModel tcmCondE = view.getJTaulaConductor().getColumnModel();
                tcmCondE.addColumn(tcC);
                Conductor condE = (Conductor) view.getJTaulaConductor().getValueAt(filaSelCond, tcmCondE.getColumnCount() - 1);
                view.getEditarIdConductorText().setText(String.valueOf(condE.get1_id_Conductor()));
                view.getEditarEdatConductorText().setText(String.valueOf(condE.get3_edat_Conductor()));
                view.getEditarCognomConductorText().setText(condE.get2_cognom_Conductor());
                view.getEditarNomConductorText().setText(condE.get4_nom_Conductor());
                TableColumnModel tcm = view.getJTaulaVehicles().getColumnModel();
                tcm.addColumn(tc);
                try {
                    if (filaSelCond != -1) {
                        ListSelectionModel selmodV = view.getJTaulaVehicles().getSelectionModel();
                        selmodV.clearSelection();
                        for (int j = 0; j < tcm.getColumnCount(); j++) {
                            int v = ((Integer) view.getJTaulaVehicles().getValueAt(j, 0));
                            if (v == condE.get5_vehicle_Conductor()) {
                                selmodV.addSelectionInterval(j, j);
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException out) {

                }
                tcm.removeColumn(tc);
                tcmCondE.removeColumn(tcC);
            }
        }
        );
        view.getEditarConductorButton().addActionListener(e -> {
//                    System.out.println(filaSel);
            carregarTaulaConductorActual();
            if (filaSelCond != -1) {
                TableColumnModel tcm = view.getJTaulaConductor().getColumnModel();
                tcm.addColumn(tc);
//                        System.out.println(filaSel);  
                Conductor cond = (Conductor) view.getJTaulaConductor().getValueAt(filaSelCond, tcm.getColumnCount() - 1);
                String cognom = view.getEditarCognomConductorText().getText();
                int edat = Integer.parseInt(view.getEditarEdatConductorText().getText());
                String nom = view.getEditarNomConductorText().getText();
                modelo.editarConductorBD(nom, cognom, edat);
                tcm.removeColumn(tc);

                carregarTaulaConductorActual();

                filaSelCond = -1;
                carregarBD();
            } else {
                System.out.println(filaSelCond);
                JOptionPane.showMessageDialog(view, "Has de seleccionar una fila per a editarla!");
                carregarTaulaVehicleActual();
            }
        }
        );

        //afegirConductor
        view.getAfegirConductorButton().addActionListener(e -> {
            if (view.getAfegirIdConductorText().getText().isBlank()
                    || view.getAfegirCognomConductorText().getText().isBlank()
                    || view.getAfegirEdatConductorText().getText().isBlank()
                    || view.getAfegirNomConductorText().getText().isBlank()) {
                JOptionPane.showMessageDialog(view, "Hi ha algun camp buit. No pot haver-hi cap camp buit!");
            } else {
                //ACAEVEEV

                carregarTaulaVehicleActual();
                carregarTaulaConductorActual();
                try {
                    if (Integer.parseInt(view.getAfegirEdatConductorText().getText()) < 18
                            || Integer.parseInt(view.getAfegirEdatConductorText().getText()) > 80) {
                        JOptionPane.showMessageDialog(view, "La edat del conductor ha d'estar entre 18 i 80 anys!");
                    } else {
                        TableColumnModel tcm = view.getJTaulaVehicles().getColumnModel();
                        tcm.addColumn(tc);
                        Vehicle veh = (Vehicle) view.getJTaulaVehicles().getValueAt(view.getNumVehicleConductorCombobox().getSelectedIndex(),
                                tcm.getColumnCount() - 1);
                        tcm.removeColumn(tc);

                        int num_veh = veh.get1_numero_Vehicle();
                        modelo.insertarConductorBD(view.getAfegirNomConductorText().getText(),
                                view.getAfegirCognomConductorText().getText(),
                                Integer.parseInt(view.getAfegirEdatConductorText().getText()),
                                Integer.parseInt(view.getAfegirIdConductorText().getText()),
                                //                                Integer.parseInt(view.getAfegirNumeroText().getText())
                                num_veh
                        );
                        carregarBD();
                    }

                } catch (NumberFormatException a) {
                    JOptionPane.showMessageDialog(view, "La edat ha de estar en format numeric!");
                } catch (SQLException a) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, a);
////                    JOptionPane.showMessageDialog(view, "Error SQL: No pot haver-hi dos registres amb la mateixa ID!");
                }
                carregarTaulaConductorActual();
                carregarTaulaVehicleActual();
            }

        }
        );
        //eliminarConductor
        view.getEliminarConductorButton().addActionListener(e -> {
//                    System.out.println(filaSel);
            if (filaSelCond != -1) {
                TableColumnModel tcm = view.getJTaulaConductor().getColumnModel();
                tcm.addColumn(tcC);
//                        System.out.println(filaSel);  
                Conductor cond = (Conductor) view.getJTaulaConductor().getValueAt(filaSelCond, tcm.getColumnCount() - 1);
//                        System.out.println(veh.toString());
                tcm.removeColumn(tcC);
                modelo.eliminarConductorBD(cond);
                carregarTaulaConductorActual();
                carregarTaulaVehicleActual();
                filaSelCond = -1;
            } else {
                System.out.println(filaSelCond);
                JOptionPane.showMessageDialog(view, "Has de seleccionar una fila per a borrarla!");
            }
            carregarBD();
        }
        );

        view.getFiltrarConductorCombobox().addItemListener(e -> {
            if (view.getFiltrarConductorCombobox().getSelectedIndex() == 0) {
                colConductorActual = 0;
                carregarTaulaConductorActual();
            }
            if (view.getFiltrarConductorCombobox().getSelectedIndex() == 1) {
                colConductorActual = 1;
                carregarTaulaConductorActual();
            }
        }
        );

        //COSA DE ACTUALITZAR LA BD SI CANVIEM ALGO DIRECTAMENT A LA TAULA OLE OLE
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        view.getJTaulaVehicles().getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // TODO fer cosa de que canvii a la BD o algo no se
                System.out.println("holaa");
            }

        });

//        view.getNumVehicleConductorCombobox().addItemListener(e -> {
//            comboboxActualCond =;
//        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    modelo.closeConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    //Per implementar els ActionEvents dels components de la vista (útil per 
    //exemple, per controlar l'acció que s'executa quan fem clic a un botó tant 
    //usant el ratolí com si l'apretem en la barra del teclat  
    static class Action implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    //Per implementar els KeyEvents
    //També podem usar un KeyAdapter
    //static class Key extends KeyAdapter{}
    static class Key implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    //Podem posar tots els listeners necessaris...
}

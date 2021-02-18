/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Properties;
import java.util.TreeSet;
import view.View;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javabeans.NewBean;

/**
 *
 * @author profe
 */
public class Model implements Serializable {

    private static View view;
    private static Collection<Equip> dades = new TreeSet<>();
    private static Collection<Equip> dades2 = new TreeSet<>(new EquipOrdenaPuntuacio());
    private static Collection<Jugador> dadesJugador = new TreeSet<>();
    private static Collection<Jugador> dadesJugador2 = new TreeSet<>(new JugadorOrdena());
    private static NewBean p = new NewBean();

    public static Collection<Equip> getDades() {
        return dades;
    }

    public static Collection<Equip> getDades2() {
        return dades2;
    }

    public static Collection<Jugador> getDadesJugador() {
        return dadesJugador;
    }

    public static Collection<Jugador> getDadesJugador2() {
        return dadesJugador2;
    }

    public Model() {
    }

    public void accessBD() throws FileNotFoundException, IOException, SQLException {
        try {
            p.setPropsDB("config.properties");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void llegirEquip() throws IOException {
        try {
            p.setSelect("select * from equip");
            ResultSet resultSet = p.getRs();
            Equip obj;
            while (resultSet.next()) {
                String NomEquip = resultSet.getString("nom");
                String GolsEnContra = resultSet.getString("gols_en_contra");
                String GolsAfavor = resultSet.getString("gols_afavor");
                String PartitsGuanyats = resultSet.getString("partits_guanyats");
                String PartitsPerduts = resultSet.getString("partits_perduts");
                String PartitsEmpats = resultSet.getString("partits_empatats");
                String PuntsEquip = resultSet.getString("punts");
                String Jornada = resultSet.getString("jornada");
                int id = resultSet.getInt("id");
                obj = Model.obtenirEquip(NomEquip, Integer.parseInt(GolsEnContra), Integer.parseInt(GolsAfavor), Integer.parseInt(PartitsGuanyats), Integer.parseInt(PartitsPerduts), Integer.parseInt(PartitsEmpats), Integer.parseInt(PuntsEquip), Integer.parseInt(Jornada));
                obj.set10_id(id);
                Model.<Equip>insertar(obj, Model.getDades());
                for (Jugador eq : obj._9_jug) {
                    Model.<Jugador>insertar(eq, Model.getDadesJugador());
                }
            }
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void llegirJugador() throws IOException {
        try {
            Equip obj2 = null;
            p.setSelect("select * from jugador");
            Jugador obj = null;
            ResultSet resultSet = p.getRs();
            while (resultSet.next()) {
                String NomJugador = resultSet.getString("nom");
                int clau = resultSet.getInt("id_equip");
                Equip eq11 = null;
                Equip obj1 = null;
                String[] a = new String[1];
                a[0] = resultSet.getString("posicio");
                String Gols = resultSet.getString("gols");
                String PartitsJugats = resultSet.getString("partits_jugats");
                System.out.println(NomJugador);
                int id = resultSet.getInt("id_equip");
                obj = Model.obtenirJugador2(NomJugador, obj1, a, Integer.parseInt(Gols), Integer.parseInt(PartitsJugats));
                for (Equip e1 : Model.getDades()) {
                    if (e1.get10_id() == id) {
                        obj2 = e1;
                    }
                }
                obj.set2_equip(obj2);
                obj.set0_idequip(id);
                for (Equip value : Model.dades) {
                    try {
                        if (value.get1_nom() == obj.get2_equip().get1_nom()) {
                            value._9_jug.add(obj);
                        }
                    } catch (Exception ex) {
                    }
                }
                Model.<Jugador>insertar(obj, Model.getDadesJugador());
            }
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void creacioTaules() {
        try {
            p.setCreatetable("CREATE DATABASE IF NOT EXISTS equip;");
            p.setCreatetable("USE equip;");
            p.setCreatetable("CREATE TABLE IF NOT EXISTS equip (nom varchar(255) DEFAULT NULL,gols_en_contra int DEFAULT NULL,gols_afavor int DEFAULT NULL,partits_guanyats int DEFAULT NULL,partits_perduts int DEFAULT NULL,partits_empatats int DEFAULT NULL,punts int DEFAULT NULL,jornada int DEFAULT NULL,ID int NOT NULL AUTO_INCREMENT,PRIMARY KEY (ID),UNIQUE KEY nom (nom));");
            p.setCreatetable("CREATE TABLE IF NOT EXISTS jugador (ID int NOT NULL AUTO_INCREMENT,nom varchar(255) DEFAULT NULL,equip varchar(255) DEFAULT NULL,posicio varchar(255) DEFAULT NULL,gols int DEFAULT NULL,partits_jugats int DEFAULT NULL,id_equip int DEFAULT NULL,PRIMARY KEY (ID),UNIQUE KEY nom (nom))");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static <T> void insertar(T eq1, Collection<T> coleccion) {
        coleccion.add(eq1);
    }

    public static void borrarEquip(Equip eq1) {
        dades.remove(eq1);
        dades2.remove(eq1);
        String x = "'";
        try {
            p.setDelete("DELETE FROM equip WHERE nom=" + x + eq1.get1_nom() + x + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Jugador j : eq1.get9_jug()) {
            j.set2_equip(null);
        }
    }

    public static void borrarJugador(Jugador j1) {
        dadesJugador.remove(j1);
        dadesJugador2.remove(j1);
        String x = "'";
        try {
            p.setDelete("DELETE FROM jugador WHERE nom=" + x + j1.get1_nomcognoms() + x + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (j1.get2_equip() != null) {
            j1.get2_equip().get9_jug().remove(j1);
        }
    }

    public static Equip obtenirEquip(String _1_nomEquip, int _2_golsEnContra, int _3_golsAfavor, int _4_partitsGuanyats, int _5_partitsPerduts, int _6_partitsEmpatats, int _7_puntsEquip, int _8_jornada) {
        Equip eq1 = new Equip(
                _1_nomEquip,
                _2_golsEnContra,
                _3_golsAfavor,
                _4_partitsGuanyats,
                _5_partitsPerduts,
                _6_partitsEmpatats,
                _7_puntsEquip,
                _8_jornada
        );
        return eq1;
    }

    public static Equip obtenirEquip2(String _1_nomEquip, int _2_golsEnContra, int _3_golsAfavor, int _4_partitsGuanyats, int _5_partitsPerduts, int _6_partitsEmpatats, int _7_puntsEquip, int _8_jornada) {
        Equip eq1 = new Equip(
                _1_nomEquip,
                _2_golsEnContra,
                _3_golsAfavor,
                _4_partitsGuanyats,
                _5_partitsPerduts,
                _6_partitsEmpatats,
                _7_puntsEquip,
                _8_jornada
        );
        String x = "'";
        try {
            p.setInsert("INSERT INTO equip(nom,gols_en_contra,gols_afavor,partits_guanyats,partits_perduts,partits_empatats,punts,jornada) VALUES (" + x + eq1.get1_nom() + x + "," + eq1.get2_golsEnContra() + "," + eq1.get3_golsAfavor() + "," + eq1.get4_partitsGuanyats() + "," + eq1.get5_partitsPerduts() + "," + eq1.get6_partitsEmpatats() + "," + eq1.get7_punts() + "," + eq1.get8_jornada() + ")" + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            p.setSelect("select * from equip WHERE nom=" + x + eq1.get1_nom() + x + ";");
            ResultSet result = p.getRs();
            int clau2 = 0;
            while (result.next()) {
                clau2 = result.getInt("id");
            }
          //  System.out.println(clau2);
            eq1.set10_id(clau2);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        Model.insertar(eq1, dades);
        Model.insertar(eq1, dades2);
        return eq1;
    }

    public static Jugador obtenirJugador(String _1_nomcognomsJugador, Equip _2_equipJugador, String[] _3_posicioJugador, int _4_golsJugador, int _5_partitsJugador) {
        Jugador jug1 = new Jugador(
                _1_nomcognomsJugador,
                _2_equipJugador,
                _3_posicioJugador,
                _4_golsJugador,
                _5_partitsJugador
        );
        int clau2 = 0;
       String x = "'";
       System.out.println(jug1.get2_equip().toString());
        try {
            //Este select aqui lo faig perque tinc el problema que et vaig ensenyar a classe
            //que si creo un equip i seguidament creo un jugador amb aquell equip no funciona
            //ja que lo p.setSelect es lo mateix al metode obtenirEquip2 i al metode obtenirJugador.
            //per tant no s'executa el vetoableChange.
            p.setSelect("select * from equip;");
            //Este es lo slect correcte
             p.setSelect("select * from equip WHERE nom=" + x + jug1.get2_equip().toString() + x + ";");
            ResultSet result = p.getRs();
            while (result.next()) {
                clau2 = result.getInt("id");
            }
            System.out.println("ESTIC ACCEDINT A LA CLAU2");
            System.out.println(clau2);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            p.setInsert("INSERT INTO jugador(nom,equip,posicio,gols,partits_jugats,id_equip) VALUES (" + x + jug1.get1_nomcognoms() + x + "," + x + jug1.get2_equip().toString() + x + "," + x + jug1.get3_posicio() + x + "," + jug1.get4_gols() + "," + jug1.get5_partits() + "," + clau2 + ")" + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(clau2);
        jug1.set0_idequip(clau2);
        Model.insertar(jug1, dadesJugador);
        Model.insertar(jug1, dadesJugador);
        return jug1;

    }

    public static Jugador obtenirJugador2(String _1_nomcognomsJugador, Equip _2_equipJugador, String[] _3_posicioJugador, int _4_golsJugador, int _5_partitsJugador) {
        Jugador jug1 = new Jugador(
                _1_nomcognomsJugador,
                _2_equipJugador,
                _3_posicioJugador,
                _4_golsJugador,
                _5_partitsJugador
        );
        return jug1;

    }

    public static void updateJugador(Equip eq1, String nom, String nomActualitzat, String nomEquip, String posicio, int gols, int partits) {
        int clau2 = 0;
        String x = "'";
        try {
            p.setSelect("select * from equip WHERE nom=" + x + eq1.get1_nom() + x + ";");
            ResultSet result = p.getRs();
            while (result.next()) {
                clau2 = result.getInt("id");
            }
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            p.setUpdate("UPDATE jugador SET nom = " + x + nomActualitzat + x + ",equip =" + x + nomEquip + x + ",posicio=" + x + posicio + x + ",gols=" + gols + ",partits_jugats=" + partits + ",id_equip=" + clau2 + " WHERE nom=" + x + nom + x + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        Jugador obj1;
        for (Jugador j1 : Model.dadesJugador) {
            if (j1.get1_nomcognoms().equals(nomActualitzat)) {
                j1.set0_idequip(clau2);
            }
        }

    }

    public static void updateEquip(String nomActualitzat, int gols_en_contra, int gols_afavor, int partits_guanyats, int partits_perduts, int partits_empatats, int punts, int jornada, String nom) {
        String x = "'";
        try {
            p.setUpdate("UPDATE equip SET nom = " + x + nomActualitzat + x + ", gols_en_contra =" + gols_en_contra + ",gols_afavor=" + gols_afavor + ",partits_guanyats=" + partits_guanyats + ",partits_perduts=" + partits_perduts + ",partits_empatats=" + partits_empatats + ",punts=" + punts + ",jornada=" + jornada + " WHERE nom=" + x + nom + x + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        int clau2 = 0;
        try {
            p.setSelect("select * from equip WHERE nom=" + x + nomActualitzat + x + ";");
            ResultSet result = p.getRs();
            while (result.next()) {
                clau2 = result.getInt("id");
            }
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            p.setUpdate("UPDATE jugador SET equip =" + x + nomActualitzat + x + " WHERE id_equip=" + clau2 + ";");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void tancarConn() throws SQLException {
        try {
            p.setClose("close");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class EquipOrdenaPuntuacio implements Comparator<Equip> {

    @Override
    public int compare(Equip o1, Equip o2) {
        return o1.get1_nom().compareTo(o2.get1_nom());
    }

}

class JugadorOrdena implements Comparator<Jugador> {

    @Override
    public int compare(Jugador o1, Jugador o2) {
        return o1.get1_nomcognoms().compareTo(o2.get1_nomcognoms());
    }
}

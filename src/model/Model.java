/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.FileInputStream;
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
    private static Properties properties = new Properties();
    private static Statement statement = null;
    private static Connection conn;
    private static PreparedStatement preparedStatement = null;

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

    public void llegirEquip() throws IOException {
        try {
            properties.load(new FileInputStream(new File("config.properties")));
            System.out.println(properties.get("driver"));
            String URL = (String) properties.get("url");
            String USER = (String) properties.get("usuari");
            String PASS = (String) properties.get("password");
            conn = DriverManager.getConnection(URL, USER, PASS);
            statement = conn.createStatement();
            ResultSet resultSet = null;
            resultSet = statement
                    .executeQuery("select * from equip");
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void llegirJugador() throws IOException {
        try {
            statement = conn.createStatement();
            ResultSet resultSet = null;
            resultSet = statement
                    .executeQuery("select * from jugador");
            Jugador obj = null;
            while (resultSet.next()) {
                String NomJugador = resultSet.getString("nom");
                int clau = resultSet.getInt("id_equip");
                Equip eq11 = null;
                preparedStatement = conn
                        .prepareStatement("select * from equip WHERE id=?");
                preparedStatement.setInt(1, clau);
                ResultSet rs = preparedStatement.executeQuery();
                Equip obj1 = null;
                while (rs.next()) {
                    String NomEquip = rs.getString("nom");
                    String GolsEnContra = rs.getString("gols_en_contra");
                    String GolsAfavor = rs.getString("gols_afavor");
                    String PartitsGuanyats = rs.getString("partits_guanyats");
                    String PartitsPerduts = rs.getString("partits_perduts");
                    String PartitsEmpats = rs.getString("partits_empatats");
                    String PuntsEquip = rs.getString("punts");
                    String Jornada = rs.getString("jornada");
                    //  Model.dades.contains(obj1);
                    for (Equip value : Model.dades) {
                        System.out.println(value.get1_nom());
                        //  System.out.println(NomEquip);
                        String x = value.get1_nom();
                        if (x.equals(NomEquip)) {
                            obj1 = value;
                        }
                    }
                    // obj1 = Model.obtenirEquip(NomEquip, Integer.parseInt(GolsEnContra), Integer.parseInt(GolsAfavor), Integer.parseInt(PartitsGuanyats), Integer.parseInt(PartitsPerduts), Integer.parseInt(PartitsEmpats), Integer.parseInt(PuntsEquip), Integer.parseInt(Jornada));
                    //  Iterable<Equip> ts = null;

                }
                String[] a = new String[1];
                a[0] = resultSet.getString("posicio");
                String Gols = resultSet.getString("gols");
                String PartitsJugats = resultSet.getString("partits_jugats");
                System.out.println(NomJugador);
                int id = resultSet.getInt("id_equip");
                obj = Model.obtenirJugador2(NomJugador, obj1, a, Integer.parseInt(Gols), Integer.parseInt(PartitsJugats));

                obj.set0_idequip(id);
                for (Equip value : Model.dades) {
                    try {
                        if (value.get1_nom() == obj.get2_equip().get1_nom()) {
                            value._9_jug.add(obj);
                        }
                    } catch (Exception ex) {
                    }
                }
                // obj1._9_jug.add(obj);

                Model.<Jugador>insertar(obj, Model.getDadesJugador());
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static <T> void insertar(T eq1, Collection<T> coleccion) {
        coleccion.add(eq1);
    }

    public static void borrarEquip(Equip eq1) {
        dades.remove(eq1);
        dades2.remove(eq1);
        try {
            statement = conn.createStatement();
            ResultSet resultSet = null;
            preparedStatement = conn
                    .prepareStatement("DELETE FROM equip WHERE nom=? ;");
            preparedStatement.setString(1, eq1.get1_nom());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Jugador j : eq1.get9_jug()) {
            j.set2_equip(null);
        }
    }

    public static void borrarJugador(Jugador j1) {
        dadesJugador.remove(j1);
        dadesJugador2.remove(j1);
        try {
            statement = conn.createStatement();
            ResultSet resultSet = null;
            preparedStatement = conn
                    .prepareStatement("DELETE FROM jugador WHERE nom=? ;");
            preparedStatement.setString(1, j1.get1_nomcognoms());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
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
        
        try {
            // conn.setAutoCommit(false);
            statement = conn.createStatement();
            ResultSet resultSet = null;
            preparedStatement = conn
                    .prepareStatement("INSERT INTO equip(nom,gols_en_contra,gols_afavor,partits_guanyats,partits_perduts,partits_empatats,punts,jornada) VALUES (?,?,?,?,?,?,?,?) ;");
            preparedStatement.setString(1, eq1.get1_nom());
            preparedStatement.setInt(2, eq1.get2_golsEnContra());
            preparedStatement.setInt(3, eq1.get3_golsAfavor());
            preparedStatement.setInt(4, eq1.get4_partitsGuanyats());
            preparedStatement.setInt(5, eq1.get5_partitsPerduts());
            preparedStatement.setInt(6, eq1.get6_partitsEmpatats());
            preparedStatement.setInt(7, eq1.get7_punts());
            preparedStatement.setInt(8, eq1.get8_jornada());
            preparedStatement.executeUpdate();
            System.out.println("hola");
        } catch (SQLException e) {

        }
        int clau2 = 0;
        try {

            statement = conn.createStatement();
            // ResultSet resultSet1 = null;
            preparedStatement = conn
                    .prepareStatement("select * from equip WHERE nom=?");
            preparedStatement.setString(1, eq1.get1_nom());
            ResultSet rs = preparedStatement.executeQuery();
            Equip obj1 = null;
            while (rs.next()) {
                clau2 = rs.getInt("id");

            }
        } catch (Exception ex) {
            //Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        eq1.set10_id(clau2);
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
        try {

            statement = conn.createStatement();
            // ResultSet resultSet1 = null;
            preparedStatement = conn
                    .prepareStatement("select * from equip WHERE nom=?");
            preparedStatement.setString(1, jug1.get2_equip().toString());
            ResultSet rs = preparedStatement.executeQuery();
            Equip obj1 = null;
            while (rs.next()) {
                clau2 = rs.getInt("id");

            }
        } catch (Exception ex) {
            //Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            statement = conn.createStatement();
            ResultSet resultSet = null;
            preparedStatement = conn
                    .prepareStatement("INSERT INTO jugador(nom,equip,posicio,gols,partits_jugats,id_equip) VALUES (?,?,?,?,?,?) ;");
            preparedStatement.setString(1, jug1.get1_nomcognoms());
            preparedStatement.setString(2, jug1.get2_equip().toString());
            preparedStatement.setString(3, jug1.get3_posicio());
            preparedStatement.setInt(4, jug1.get4_gols());
            preparedStatement.setInt(5, jug1.get5_partits());
            preparedStatement.setInt(6, clau2);
            preparedStatement.executeUpdate();
            //   conn.close();
        } catch (Exception ex) {
            //Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            statement = conn.createStatement();
            ResultSet resultSet = null;
            preparedStatement = conn
                    .prepareStatement("select * from jugador WHERE nom=?;");
            preparedStatement.setString(1, jug1.get1_nomcognoms());
            resultSet = preparedStatement.executeQuery();
            //   conn.close();
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                jug1.set0_idequip(id);
            }
        } catch (Exception ex) {
            //Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        //  int id = 0;
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
        try {

            statement = conn.createStatement();
            preparedStatement = conn
                    .prepareStatement("select * from equip WHERE nom=?");
            preparedStatement.setString(1, eq1.get1_nom());
            ResultSet rs = preparedStatement.executeQuery();
            Equip obj1;
            while (rs.next()) {
                clau2 = rs.getInt("id");
            }
        } catch (Exception ex) {
            //Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            statement = conn.createStatement();
            ResultSet resultSet = null;
            preparedStatement = conn
                    .prepareStatement("UPDATE jugador SET nom = ?, equip = ?,posicio=?,gols=?,partits_jugats=?,id_equip=? WHERE nom=?;");
            preparedStatement.setString(1, nomActualitzat);
            preparedStatement.setString(2, nomEquip);
            preparedStatement.setString(3, posicio);
            preparedStatement.setInt(4, gols);
            preparedStatement.setInt(5, partits);
            preparedStatement.setInt(6, clau2);
            preparedStatement.setString(7, nom);
            preparedStatement.executeUpdate();
            //   conn.close();
        } catch (Exception ex) {
            //Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        Jugador obj1;
        for (Jugador j1 : Model.dadesJugador) {
            if (j1.get1_nomcognoms().equals(nomActualitzat)) {
                j1.set0_idequip(clau2);
                //e.set1_nom(dada);
            }
        }

    }

    public static void updateEquip(String nomActualitzat, int gols_en_contra, int gols_afavor, int partits_guanyats, int partits_perduts, int partits_empatats, int punts, int jornada, String nom) {

        try {
            conn.setAutoCommit(false);
            statement = conn.createStatement();
            preparedStatement = conn
                    .prepareStatement("UPDATE equip SET nom = ?, gols_en_contra = ?,gols_afavor=?,partits_guanyats=?,partits_perduts=?,partits_empatats=?,punts=?,jornada=? WHERE nom=?;");
            preparedStatement.setString(1, nomActualitzat);
            preparedStatement.setInt(2, gols_en_contra);
            preparedStatement.setInt(3, gols_afavor);
            preparedStatement.setInt(4, partits_guanyats);
            preparedStatement.setInt(5, partits_perduts);
            preparedStatement.setInt(6, partits_empatats);
            preparedStatement.setInt(7, punts);
            preparedStatement.setInt(8, jornada);
            preparedStatement.setString(9, nom);
            preparedStatement.executeUpdate();
            int clau2 = 0;
            preparedStatement = conn
                    .prepareStatement("select * from equip WHERE nom=?");
            preparedStatement.setString(1, nomActualitzat);
            ResultSet rs = preparedStatement.executeQuery();
            Equip obj1;
            while (rs.next()) {
                clau2 = rs.getInt("id");
            }
            ResultSet resultSet = null;
            preparedStatement = conn
                    .prepareStatement("UPDATE jugador SET equip = ? WHERE id_equip=?;");
            preparedStatement.setString(1, nomActualitzat);
            preparedStatement.setInt(2, clau2);
            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException e) { 
            System.out.println(e.toString());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }
            }
        }
    }
    public static void tancarConn() throws SQLException {
        conn.close();
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

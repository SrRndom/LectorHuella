package com.huella.lector;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fid.Format;
//import com.digitalpersona.uareu.Reader.CaptureQuality;
import com.digitalpersona.uareu.Reader.CaptureResult;
import com.digitalpersona.uareu.Reader.Priority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FingerprintMultiManager {
    private ReaderCollection readers;
    private Reader reader;
    private Engine engine;
    private Fmd enrollmentFmd; // Almacenar el FMD enrolado en memoria
    int threshold = 10000;  //Tiempo de Espera
    

    public FingerprintMultiManager() throws UareUException {
        engine = UareUGlobal.GetEngine();
        readers = UareUGlobal.GetReaderCollection();
        readers.GetReaders();
        if (readers.size() == 0) {
            System.err.println("No se encontraron lectores.");
            System.exit(1);
        }
        reader = readers.get(0); // Tomar el primer lector disponible
    }

    public void initializeReader() throws UareUException {
        reader.Open(Priority.EXCLUSIVE);
        System.out.println("Lector iniciado: " + reader.GetStatus());
    }

    public void captureAndEnrollFingerprint() throws UareUException {
        System.out.println("Coloca tu dedo en el lector para enrolar...");

        CaptureResult captureResult = reader.Capture(Format.ISO_19794_4_2005, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
        if (captureResult.quality == Reader.CaptureQuality.GOOD) {
            System.out.println("Captura realizada con éxito.");
            enrollmentFmd = engine.CreateFmd(captureResult.image, Fmd.Format.ISO_19794_2_2005);
            System.out.println("FMD creado para enrolamiento y guardado en memoria.");
        } else {
            System.err.println("La calidad de la captura no es buena: " + captureResult.quality.toString());
        }
    }

    public boolean verifyFingerprint() throws UareUException {
        System.out.println("Coloca tu dedo en el lector para verificar...");

        CaptureResult captureResult = reader.Capture(Format.ISO_19794_4_2005, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
        if (captureResult.quality == Reader.CaptureQuality.GOOD) {
            System.out.println("Captura para verificación realizada con éxito.");

            Fmd verificationFmd = engine.CreateFmd(captureResult.image, Fmd.Format.ISO_19794_2_2005);
            int falsematch_rate = engine.Compare(enrollmentFmd, 0, verificationFmd, 0);

            // Un valor igual a 0 indica que las huellas coinciden [Via Debug se puede verificar]
            if (falsematch_rate < threshold) {
                System.out.println("Las huellas dactilares coinciden.");
                return true;
            } else {
                System.out.println("Las huellas dactilares no coinciden.");
                return false;
            }
        } else {
            System.err.println("La calidad de la captura no es buena: " + captureResult.quality.toString());
            return false;
        }
    }
    
    ///Metodos de guardado
    
    public void saveFingerprintToFMD(Fmd fmd, int userId, String tipoDedo, String userName) {
        String sql = "INSERT INTO Huellas (userid, tipo_dedo, fmd) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Verificar si el usuario ya existe, si no, crearlo
            int actualUserId = checkOrCreateUser(userId, userName);
            
            byte[] fmdData = fmd.getData();
            stmt.setInt(1, actualUserId);
            stmt.setString(2, tipoDedo);
            stmt.setBytes(3, fmdData);
            stmt.executeUpdate();
            System.out.println("Huella dactilar almacenada con éxito.");
        } catch (SQLException e) {
            System.err.println("Error al guardar la huella dactilar: " + e.getMessage());
        }
    }

    // Método para verificar si el usuario existe, si no lo creara
    private int checkOrCreateUser(int userId, String userName) throws SQLException {
        String sqlCheck = "SELECT userid FROM Usuarios WHERE username = ?";
        String sqlInsert = "INSERT INTO Usuarios (username) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertStmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            
            checkStmt.setString(1, userName);
            ResultSet rs = checkStmt.executeQuery();
            if(rs.next()) {
                return rs.getInt("userid");
            } else {
                insertStmt.setString(1, userName);
                insertStmt.executeUpdate();
                ResultSet rsInsert = insertStmt.getGeneratedKeys();
                if(rsInsert.next()) {
                    return rsInsert.getInt(1); // Retorna el nuevo userID generado
                } else {
                    throw new SQLException("Error al crear el usuario.");
                }
            }
        }
    }

    public Fmd retrieveFingerprintByUserId(int userId) {
        String sql = "SELECT fmd FROM Huellas WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                byte[] storedFmdData = rs.getBytes("fmd");
                return UareUGlobal.GetImporter().ImportFmd(storedFmdData, Fmd.Format.ISO_19794_2_2005, Fmd.Format.ISO_19794_2_2005);
            }
        } catch (SQLException | UareUException e) {
            System.err.println("Error al recuperar la huella dactilar: " + e.getMessage());
        }
        return null;
    }
    
   //Metodos para tratar de llamar todas las huellas disponibles, aun en pruebas
    public Fmd[] retrieveAllFingerprints() {
        List<Fmd> fmdList = new ArrayList<>();
        String sql = "SELECT fmd FROM fingerprints";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                byte[] storedFmdData = rs.getBytes("fmd");
                Fmd fmd = UareUGlobal.GetImporter().ImportFmd(storedFmdData, Fmd.Format.ISO_19794_2_2005, Fmd.Format.ISO_19794_2_2005);
                fmdList.add(fmd);
            }
            return fmdList.toArray(new Fmd[0]);
        } catch (SQLException | UareUException e) {
            System.err.println("Error al recuperar todas las huellas dactilares: " + e.getMessage());
            return null;
        }
    }
    
    public void identifyFingerprint(Fmd[] candidateFmds) throws UareUException {
        System.out.println("Coloca tu dedo en el lector para la identificación...");
        CaptureResult captureResult = reader.Capture(Format.ISO_19794_4_2005, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
        if (captureResult.quality == Reader.CaptureQuality.GOOD) {
            System.out.println("Captura realizada con éxito.");
            Fmd verificationFmd = engine.CreateFmd(captureResult.image, Fmd.Format.ISO_19794_2_2005);
            Engine.Candidate[] candidates = engine.Identify(verificationFmd, 0, candidateFmds, threshold, 1);
            if (candidates.length > 0) {
                System.out.println("Huella verificada. Coincide con el ID de usuario en la base de datos.");
            } else {
                System.out.println("No se encontraron coincidencias en la base de datos.");
            }
        } else {
            System.err.println("La calidad de la captura no es buena: " + captureResult.quality.toString());
        }
    }

    public List<Fmd> retrieveFingerprintsByUserId(int userId) {
        String sql = "SELECT fmd FROM Huellas WHERE userid = ?";
        List<Fmd> fmdList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                byte[] storedFmdData = rs.getBytes("fmd");
                Fmd fmd = UareUGlobal.GetImporter().ImportFmd(storedFmdData, Fmd.Format.ISO_19794_2_2005, Fmd.Format.ISO_19794_2_2005);
                fmdList.add(fmd);
            }
        } catch (SQLException | UareUException e) {
            System.err.println("Error al recuperar las huellas dactilares: " + e.getMessage());
        }
        return fmdList;
    }


    public void closeReader() throws UareUException {
        reader.Close();
        System.out.println("Lector cerrado.");
    }

    public static void main(String[] args) {
        try {
            FingerprintMultiManager example = new FingerprintMultiManager();
            example.initializeReader();

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Enrolar nueva huella");
                System.out.println("2. Verificar huella existente");
                System.out.println("3. Validacion Directa");
                System.out.println("4. Salir");
                System.out.print("Opción: ");
                int option = scanner.nextInt();

                switch (option) {
                    case 1:
                        scanner.nextLine(); // Limpiar buffer de entrada
                        System.out.print("Ingrese el ID de usuario para enrolar: ");
                        int enrollUserId = scanner.nextInt(); scanner.nextLine(); // Consumir el salto de línea

                        System.out.print("Ingrese el nombre de usuario para enrolar: ");
                        String enrollUserName = scanner.nextLine();

                        example.captureAndEnrollFingerprint();
                        example.saveFingerprintToFMD(example.enrollmentFmd, enrollUserId, enrollUserName, enrollUserName);
                        break;
                    case 2:
                        scanner.nextLine(); // Limpiar buffer de entrada
                        System.out.print("Ingrese el ID de usuario para verificar: ");
                        int verifyUserId = scanner.nextInt(); scanner.nextLine(); // Asegurarse de consumir el resto de la línea como si fuese polvo 

                        Fmd fmdFromDB = example.retrieveFingerprintByUserId(verifyUserId);
                        if (fmdFromDB != null) {
                            example.enrollmentFmd = fmdFromDB; // Asegurarse de usar el FMD correcto para la verificación
                            boolean verified = example.verifyFingerprint();
                            System.out.println("Resultado de verificación: " + verified);
                        } else {
                            System.out.println("No se encontró un FMD para el userID proporcionado.");
                        }
                        break;
                    case 3:
                    	System.out.println("Coloca tu dedo en el lector para verificar contra todas las huellas registradas...");
                        Fmd[] allFmdsFromDB = example.retrieveAllFingerprints();
                        if (allFmdsFromDB != null && allFmdsFromDB.length > 0) {
                            example.identifyFingerprint(allFmdsFromDB); // Nuevo método para identificar huella
                        } else {
                            System.out.println("No hay huellas registradas para comparar.");
                        }
                        break;
                    case 4:
                        exit = true;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            }

            example.closeReader();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }


}
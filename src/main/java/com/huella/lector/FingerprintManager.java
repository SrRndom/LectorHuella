package com.huella.lector;//Actual Funcional 

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fid.Format;
import com.digitalpersona.uareu.Reader.CaptureResult;
import com.digitalpersona.uareu.Reader.Priority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;

public class FingerprintManager {
    private ReaderCollection readers;
    private Reader reader;
    private Engine engine;
    private Fmd enrollmentFmd; // Almacenar el FMD enrolado en memoria
    int threshold = 10000;
    

    public FingerprintManager() throws UareUException {
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

            // Un valor cercano a 0 indica que las huellas coinciden [Via Debug se puede verificar]
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
    
    public void saveFingerprintToFMD(Fmd fmd, int userId) {
        String sql = "INSERT INTO fingerprints (fmd, userId) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            byte[] fmdData = fmd.getData();
            stmt.setBytes(1, fmdData);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            System.out.println("Huella dactilar almacenada con éxito.");
        } catch (SQLException e) {
            System.err.println("Error al guardar la huella dactilar: " + e.getMessage());
        }
    }

    public Fmd retrieveFingerprintByUserId(int userId) {
        String sql = "SELECT fmd FROM fingerprints WHERE userId = ?";
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

    public void closeReader() throws UareUException {
        reader.Close();
        System.out.println("Lector cerrado.");
    }

//    public static void main(String[] args) {
//        try {
//            FingerprintManager example = new FingerprintManager();
//            example.initializeReader();
//            example.captureAndEnrollFingerprint(); // Enrolar huella
//            boolean verified = example.verifyFingerprint(); // Verificar huella
//            System.out.println("Resultado de verificación: " + verified);
//            example.closeReader();
//        } catch (UareUException e) {
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) {
        try {
            FingerprintManager example = new FingerprintManager();
            example.initializeReader();
            
            // Asumimos un ID de usuario arbitrario para este ejemplo
            int userId = 1;
            
            // Captura y enrola la huella dactilar del usuario
            example.captureAndEnrollFingerprint();
            
            // Guarda la huella enrolada (FMD) en la base de datos asociada con el userId
            example.saveFingerprintToFMD(example.enrollmentFmd, userId);
            
            // Simula un flujo donde necesitas recuperar el FMD almacenado y verificarlo con una nueva captura
            // Por ejemplo, esto podría ser en un nuevo inicio de sesión o una verificación de identidad
            Fmd fmdFromDB = example.retrieveFingerprintByUserId(userId);
            if (fmdFromDB != null) {
                example.enrollmentFmd = fmdFromDB; // Actualiza el FMD enrolado con el recuperado de la BD
                boolean verified = example.verifyFingerprint(); // Verifica la nueva huella contra el FMD recuperado
                System.out.println("Resultado de verificación: " + verified);
            } else {
                System.out.println("No se encontró un FMD para el userID proporcionado.");
            }
            
            example.closeReader();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

}
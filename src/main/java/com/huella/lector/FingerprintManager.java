package com.huella.lector;//Actual Funcional 

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fid.Format;
import com.digitalpersona.uareu.Reader.CaptureResult;
import com.digitalpersona.uareu.Reader.Priority;

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
            enrollmentFmd = engine.CreateFmd(captureResult.image, Fmd.Format.ANSI_378_2004);
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

    public void closeReader() throws UareUException {
        reader.Close();
        System.out.println("Lector cerrado.");
    }

    public static void main(String[] args) {
        try {
            FingerprintManager example = new FingerprintManager();
            example.initializeReader();
            example.captureAndEnrollFingerprint(); // Enrolar huella
            boolean verified = example.verifyFingerprint(); // Verificar huella
            System.out.println("Resultado de verificación: " + verified);
            example.closeReader();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }
}
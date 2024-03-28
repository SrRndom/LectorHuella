package com.huella.lector;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fid.Format;
import com.digitalpersona.uareu.Reader.CaptureResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AdvancedFingerprint {
    private ReaderCollection readers;
    private Reader reader;
    private Engine engine;
    private List<String> enrolledFmdFilePaths = new ArrayList<>();
    private final static String FMD_FILES_DIRECTORY = "C:\\Users\\HP PROBOOK\\eclipse-workspace\\lector\\fmd_locales\\";

    public AdvancedFingerprint() throws UareUException {
        engine = UareUGlobal.GetEngine();
        readers = UareUGlobal.GetReaderCollection();
        readers.GetReaders();
        if (readers.size() == 0) {
            System.err.println("No se encontraron lectores.");
            System.exit(1);
        }
        reader = readers.get(0);
    }

    public void initializeReader() throws UareUException {
        reader.Open(Reader.Priority.EXCLUSIVE);
        System.out.println("Lector iniciado: " + reader.GetDescription().serial_number);
    }

    public Fmd captureAndCreateFmd() throws UareUException {
        System.out.println("Coloca tu dedo en el lector...");

        CaptureResult captureResult = reader.Capture(Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
        if (captureResult.quality == Reader.CaptureQuality.GOOD) {
            System.out.println("Captura realizada con éxito.");
            return engine.CreateFmd(captureResult.image, Fmd.Format.ANSI_378_2004);
        } else {
            System.err.println("La calidad de la captura no es buena: " + captureResult.quality.toString());
            return null;
        }
    }

    public void enrollFmd(Fmd fmd) throws IOException {
        if (fmd != null) {
            String fmdFilePath = FMD_FILES_DIRECTORY + "FMD_" + System.currentTimeMillis() + ".fmd";
            Files.write(Paths.get(fmdFilePath), fmd.getData());
            enrolledFmdFilePaths.add(fmdFilePath);
            System.out.println("Huella dactilar enrolada y guardada con éxito en " + fmdFilePath);
        }
    }

//    public boolean verifyFingerprint(Fmd fmdToVerify) throws UareUException {
//        Fmd capturedFmd = captureAndCreateFmd();
//        if (capturedFmd != null && fmdToVerify != null) {
//            int falsematch_rate = engine.Compare(fmdToVerify, 0, capturedFmd, 0);
//            return falsematch_rate < (Fmd.FMV_PROBABILITY_ONE / 100000);
//        } else {
//            return false;
//        }
//    }
    public boolean verifyFingerprint(Fmd originalFmd) throws UareUException {
        System.out.println("Vuelve a colocar tu dedo en el lector para verificación...");

        CaptureResult captureResult = reader.Capture(Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
        if (captureResult.quality == Reader.CaptureQuality.GOOD) {
            System.out.println("Captura para verificación realizada con éxito.");

            Fmd verificationFmd = engine.CreateFmd(captureResult.image, Fmd.Format.ANSI_378_2004);
            int falsematch_rate = engine.Compare(originalFmd, 0, verificationFmd, 0);

            // De acuerdo con la documentación, un valor cercano a 0 indica que las huellas coinciden
            return (falsematch_rate < (DPFPDD_FMD_ANSI_378_2004.MIN_FALSEMATCH_RATE / 100000));
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
            AdvancedFingerprint example = new AdvancedFingerprint();
            example.initializeReader();

            Fmd enrolledFmd = example.captureAndCreateFmd();
            example.enrollFmd(enrolledFmd);

            // Suponiendo que se quiera realizar la verificación inmediatamente después del enrolamiento
            boolean verificationResult = example.verifyFingerprint(enrolledFmd);
            System.out.println("Resultado de la verificación: " + (verificationResult ? "VERIFICADO" : "NO VERIFICADO"));

            example.closeReader();
        } catch (UareUException | IOException e) {
            e.printStackTrace();
        }
    }
}

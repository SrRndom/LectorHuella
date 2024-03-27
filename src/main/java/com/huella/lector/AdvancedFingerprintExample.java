package com.huella.lector;//PRUEBA 9

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fid.Format;
import com.digitalpersona.uareu.Reader.CaptureResult;
import java.util.ArrayList;
import java.util.List;

public class AdvancedFingerprintExample {
    private ReaderCollection readers;
    private Reader reader;
    private Engine engine;
    private List<Fmd> enrolledFmds = new ArrayList<>(); // Lista para almacenar FMDs enrolados
    private final static int FALSE_MATCH_RATE_THRESHOLD = 30000; // Umbral para considerar si una huella coincide o no

    public AdvancedFingerprintExample() throws UareUException {
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
        reader.Open(Reader.Priority.EXCLUSIVE);
        System.out.println("Lector iniciado: " + reader.GetStatus());
    }

    public void captureAndEnrollFingerprint() throws UareUException {
        System.out.println("Coloca tu dedo en el lector...");

        CaptureResult captureResult = reader.Capture(Format.ISO_19794_4_2005, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
        if (captureResult.quality == Reader.CaptureQuality.GOOD) {
            System.out.println("Captura realizada con éxito.");

            Fmd fmd = engine.CreateFmd(captureResult.image, Fmd.Format.ISO_19794_2_2005);
            System.out.println("FMD creado para enrolamiento.");

            // Verificar si la huella ya está enrolada
            boolean isAlreadyEnrolled = false;
            for (Fmd enrolledFmd : enrolledFmds) {
                int falsematch_rate = engine.Compare(enrolledFmd, 0, fmd, 0);
                if (falsematch_rate < FALSE_MATCH_RATE_THRESHOLD) {
                    isAlreadyEnrolled = true;
                    break;
                }
            }

            if (!isAlreadyEnrolled) {
                enrolledFmds.add(fmd);
                System.out.println("Huella dactilar enrolada con éxito.");
            } else {
                System.out.println("Esta huella ya está enrolada.");
            }
        } else {
            System.err.println("La calidad de la captura no es buena: " + captureResult.quality.toString());
        }
    }

    public void closeReader() throws UareUException {
        reader.Close();
        System.out.println("Lector cerrado.");
    }

    public static void main(String[] args) {
        try {
            AdvancedFingerprintExample example = new AdvancedFingerprintExample();
            example.initializeReader();
            example.captureAndEnrollFingerprint();
            example.closeReader();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }
}

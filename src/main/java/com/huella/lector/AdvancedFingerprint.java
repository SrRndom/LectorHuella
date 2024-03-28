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
    private List<String> enrolledFmdFilePaths = new ArrayList<>(); // Almacena las rutas de los archivos de FMDs enrolados
    private final static String FMD_FILES_DIRECTORY = "C:\\Users\\HP PROBOOK\\eclipse-workspace\\lector\\fmd_locales\\"; 
    // Ruta local por el momento para pruebas

    public AdvancedFingerprint() throws UareUException {
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

            // Guardar el FMD en un archivo
            String fmdFilePath = FMD_FILES_DIRECTORY + "FMD_" + System.currentTimeMillis() + ".bin";
            saveFmdAsBytes(fmd, fmdFilePath);

            // Añadir la ruta del archivo a la lista de FMDs enrolados
            enrolledFmdFilePaths.add(fmdFilePath);

            System.out.println("Huella dactilar enrolada y guardada con éxito.");
        } else {
            System.err.println("La calidad de la captura no es buena: " + captureResult.quality.toString());
        }
    }

    private void saveFmdAsBytes(Fmd fmd, String filepath) {
        try {
            Files.write(Paths.get(filepath), fmd.getData());
            System.out.println("El FMD se ha guardado en: " + filepath);
        } catch (IOException e) {
            System.err.println("Error al guardar el FMD: " + e.getMessage());
        }
    }
    
 
    
//    private Fmd loadFmdFromFile (String filepath) throws IOException, UareUException{
//    	byte[] fmdData = Files.readAllBytes(Paths.get(filepath));
//    	return engine.CreateFmd(fmdData, Fmd.Format.ISO_19794_2_2005);
//    }
//    
//    public void compareFingerprints() throws IOException, UareUException{
//    	Fmd fmdToCompare =  null;
//    }

    public void closeReader() throws UareUException {
        reader.Close();
        System.out.println("Lector cerrado.");
    }

    public static void main(String[] args) {
        try {
            AdvancedFingerprint example = new AdvancedFingerprint();
            example.initializeReader();
            example.captureAndEnrollFingerprint();
            example.closeReader();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }
}

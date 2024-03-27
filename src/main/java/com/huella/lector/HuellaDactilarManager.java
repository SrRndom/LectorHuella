package com.huella.lector;//PRUEBA 4

import com.digitalpersona.uareu.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HuellaDactilarManager implements ActionListener {
    private Reader reader;
    private Engine engine;
    private Fmd enrollmentFmd; // FMD para el enrolamiento

    public HuellaDactilarManager(Reader reader) throws UareUException {
        this.reader = reader;
        this.engine = UareUGlobal.GetEngine();
        reader.Open(Reader.Priority.EXCLUSIVE);
    }

    public void iniciarCaptura() {
        CaptureThread captureThread = new CaptureThread(reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        captureThread.start(this); // Inicia la captura y escucha los eventos de captura
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e instanceof CaptureThread.CaptureEvent) {
            procesarEventoDeCaptura((CaptureThread.CaptureEvent) e);
        }
    }

    private void procesarEventoDeCaptura(CaptureThread.CaptureEvent evt) {
        if (evt.capture_result != null && evt.capture_result.quality == Reader.CaptureQuality.GOOD) {
            try {
                Fmd fmd = engine.CreateFmd(evt.capture_result.image, Fmd.Format.ANSI_378_2004);
                if (fmd != null) {
                    enrollmentFmd = fmd;
                    System.out.println("Huella dactilar enrolada con éxito.");
                }
            } catch (UareUException ex) {
                System.err.println("Error al procesar la huella dactilar: " + ex.getMessage());
            }
        } else {
            System.err.println("La captura de la huella dactilar fue de mala calidad o se canceló.");
        }
    }

    public void finalizar() {
        try {
            if (reader != null) {
                reader.Close();
            }
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();
            if (readers.size() > 0) {
                Reader reader = readers.get(0);
                HuellaDactilarManager manager = new HuellaDactilarManager(reader);
                manager.iniciarCaptura();

                
                manager.finalizar(); // Cierra el lector mamon
            } else {
                System.out.println("No se detectó ningún lector de huellas dactilares.");
            }
        } catch (UareUException e) {
            System.err.println("Error inicializando el sistema de huellas dactilares: " + e.getMessage());
        }
    }
}

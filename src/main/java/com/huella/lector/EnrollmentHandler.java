package com.huella.lector;//DEPRECATED

import com.digitalpersona.uareu.*;
//import com.digitalpersona.uareu.Engine.PreEnrollmentFmd;

public class EnrollmentHandler implements Engine.EnrollmentCallback {
    private Engine engine;
	private Reader reader;

    public EnrollmentHandler(Engine engine, Reader reader) {
        this.engine = engine;
        this.reader = reader;
    }

//    @Override
//    public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format) {
//        Fid fidImage = Captura.captureFingerprint();
//        if (fidImage == null) {
//            return null; // Aquí manejas la cancelación o fallo en la captura
//        }
//
//        try {
//            Fmd fmd = engine.CreateFmd(fidImage, format);
//            return new Engine.PreEnrollmentFmd() {
//                public Fmd getFmd() {
//                    return fmd;
//                }
//
//                public int getFingerPosition() {
//                    return 0; // Ajusta según sea necesario
//                }
//            };
//        } catch (UareUException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
    
    @Override
    public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format) {
        Fid fidImage = null;
        try {
            System.out.println("Por favor, coloca firmemente tu dedo en el lector");
            Reader.CaptureResult cr = reader.Capture(Fid.Format.ISO_19794_4_2005, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
            if (cr.quality == Reader.CaptureQuality.GOOD) {
                fidImage = cr.image;
            } else {
                // Manejar calidades de captura no buenas o repetir la captura según sea necesario
                return null;
            }

            final Fmd fmd = engine.CreateFmd(fidImage, format);
            return new Engine.PreEnrollmentFmd() {
                public Fmd getFmd() {
                    return fmd;
                }

//                public int getFingerPosition() {
//                    return 0; // Aquí hay que definir la posición del dedo si es relevante
//                }
            };
        } catch (UareUException e) {
            e.printStackTrace();
            return null;
        }
    }
}


package com.huella.lector;


import com.digitalpersona.uareu.*;

//import java.awt.event.ActionListener;

public class Captura {
	
	private static Reader reader = null;
	private static Engine engine = null;
	
	/**
	 * 
	 */
	public static void initializeReader() {
        try {
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();
            if (readers.size() == 1) {
                reader = readers.get(0); // Busca el lector y usa el unico conectado
                reader.Open(Reader.Priority.EXCLUSIVE); //Abre y toma el unico lector dispoible
                Reader.Status status = reader.GetStatus();
                System.out.println("Estado del Lector: " + status.status);//Regresa Status de Lector
            } else {
                System.out.println("No se encontró el lector o hay múltiples lectores conectados VERIFIQUE CONEXION.");//Esta por demass
                }
        } catch (UareUException e) {
            e.printStackTrace();
            // excepciones específicas del SDK
        }
    }
	
	public static Fid captureFingerprint() {
	    Fid fid = null;
	    try {
	        if (reader != null) {
	            System.out.println("Por favor, coloca firmemente tu dedo en el lector");
	            // Inicia un bucle que intentará capturar la huella dactilar
	            while (true) {
	                Reader.CaptureResult cr = reader.Capture(
	                        Fid.Format.ISO_19794_4_2005, //FORMATO ISO CON ALCANCE GLOBAL	                        
	                        Reader.ImageProcessing.IMG_PROC_DEFAULT, // Procesamiento de imagen por defecto
	                        500, // Resolución en DPI(MAXIMA)
	                        -1   // Timeout, -1 para indefinido
	                );

//	                // Verifica la calidad de la captura
//	                if (cr.quality == Reader.CaptureQuality.GOOD) {
//	                    System.out.println("Captura de huella exitosa.");
//	                    fid = cr.image; // Guarda la imagen de la huella
//	                    break; // Sal del bucle si la captura es exitosa
//	                } else {
//	                    System.out.println("Captura no exitosa. Intenta nuevamente...");
//	                    // Puedes decidir si quieres que el usuario intente nuevamente automáticamente
//	                    // o si prefieres que realice alguna acción para reintentar.
//	                }
//	            }
//	        }
//	    } catch (UareUException e) {
//	        System.err.println("Error al capturar la huella dactilar: " + e.getMessage());
//	        e.printStackTrace();
//	    }
//	    return fid; // Devuelve la imagen de la huella o null si no se pudo capturar
//	}	

	                switch (cr.quality) {
                    case GOOD:
                        System.out.println("Captura de huella exitosa.");
                        fid = cr.image;
                        return fid;
                    case CANCELED:
                        System.out.println("Captura cancelada.");
                        return null;
                    case FAKE_FINGER:
                        System.out.println("Se detectó un dedo falso (O MUERTO).");
                        break;
                    case NO_FINGER:
                        System.out.println("No se detectó el dedo. EL MOCHO");
                        break;
                    case READER_DIRTY:
                        System.out.println("El lector necesita limpieza. PUERCO XDD");
                        break;
                    case FINGER_TOO_LEFT:
                    case FINGER_TOO_RIGHT:
                    case FINGER_TOO_HIGH:
                    case FINGER_TOO_LOW:
                        System.out.println("El dedo no está colocado correctamente en el lector. PONGALO BIEN");
                        break;
                    case TIMED_OUT:
                        System.out.println("La operación ha excedido el tiempo de espera.");
                        return null;
                    // Casos segun la ocasion
                    default:
                        System.out.println("Error desconocido. Intenta de nuevo.");
                        break;
                }
                // Da al usuario la oportunidad de ajustar su dedo o solucionar el problema antes de reintentar
                Thread.sleep(1000); // Espera un segundo antes de reintentar
            }
        }
    } catch (UareUException e) {
        System.err.println("Error al capturar la huella dactilar: " + e.getMessage());
        e.printStackTrace();
    } catch (InterruptedException e) {
        System.err.println("La captura fue interrumpida.");
        Thread.currentThread().interrupt();
    }
    return null;
}
    public static Fmd createFmdFromFid(Fid fid) {
        Fmd fmd = null;
        try {
            fmd = engine.CreateFmd(fid, Fmd.Format.ISO_19794_2_2005);
        } catch (UareUException e) {
            System.err.println("Error al crear FMD desde FID: " + e.getMessage());
            e.printStackTrace();
        }
        return fmd;
    }
	                
	                
    public static void closeReader() {	//Sera util mas adelante
        if (reader != null) {
            try {
                reader.Close();
                System.out.println("Lector cerrado correctamente.");
            } catch (UareUException e) {
                e.printStackTrace();
                // excepciones específicas del SDK al cerrar el lector
            }
        }
    }
    
    public static void main(String[] args) {
        initializeReader();
        Fid fingerprintImage = captureFingerprint(); // Asegúrate de añadir esta línea
        // Procesa la imagen capturada como sea necesario
        closeReader(); // Limpieza al final
    }
}

package com.huella.lector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fid.Format;

public class FingerprintManager {
	private Engine engine;
	
	public FingerprintManager() throws UareUException{
		this.engine = UareUGlobal.GetEngine();
	}
	
	public void getFmdAsFile (Fmd fmd, String filepath) throws IOException{
		Files.write(Paths.get(filepath),fmd.getData());
		System.out.println("FMD Guardado en: " + filepath);
	}
	
	public Fmd loadFromFile(String filepath) throws UareUException {
		byte[] fmdData = Files.readAllBytes(Paths.get(filepath));
		return engine.CreateFmd(fmdData, Format.ISO_19794_4_2005);
	}
	
	public void compareFmds(Fmd fmd1, Fmd fmd2) throws UareUException{
		int falseMatchRate = engine.Compare(fmd1, 0, fmd2, 0);
		if (falseMatchRate < Engine.PROBABILITY_ONE / 10000) {
			System.out.println("La Huella Dactilar Registrada Coincide con el Registro.");
		} else {
			System.out.println("La Huella Dactilar No Coincide");
		}
	}
}

package com.huella.lector;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;

import com.digitalpersona.uareu.*;

//import java.awt.event.ActionListener;

public class Captura {
	
	private static final long serialVersionUID =2;
	
	private JDialog       m_dlgParent;
	private CaptureThread m_capture;
	private Reader        m_reader;
	private ImagePanel    m_image;
	private boolean       m_bStreaming;
	
	private void Capture(Reader reader, boolean bStreaming) { //void temporal
		m_reader =  reader;
		m_bStreaming = bStreaming;
		
		m_capture = new CaptureThread(m_reader, m_bStreaming, Fid.Format.ANSI_381_2004,Reader.ImageProcessing.IMG_PROC_DEFAULT);
		
		final int vgap = 5;
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		
		m_image - newImage();
		Dimension dm = new Dimension(400,500);
		m_image.SetPreferredSize(dm);
		add(m_image);
		add(Box.createVerticalStrut(vgap));
		
	}
	 
}

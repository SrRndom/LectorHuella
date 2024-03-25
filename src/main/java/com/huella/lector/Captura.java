package com.huella.lector;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.digitalpersona.uareu.*;

//import java.awt.event.ActionListener;

public class Captura extends JPanel implements ActionListener{
	
	private static final long serialVersionUID =2;
	
	private JDialog       m_dlgParent;
	private CaptureThread m_capture;
	private Reader        m_reader;
	private ImagePanel    m_image;
	private boolean       m_bStreaming;
	
	private Capture(Reader reader, boolean bStreaming){
		m_reader = reader;
		m_bStreaming = bStreaming;
		
		m_capture = new CaptureThread(m_reader, m_bStreaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);

		final int vgap = 5;
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		m_image = new ImagePanel();
		Dimension dm = new Dimension(400, 500);
		m_image.setPreferredSize(dm);
		add(m_image);
		add(Box.createVerticalStrut(vgap));
		
		JButton btnBack = new JButton("Back");
		btnBack.setActionCommand(ACT_BACK);
		btnBack.addActionListener(this);
		add(btnBack);
		add(Box.createVerticalStrut(vgap));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	 
}

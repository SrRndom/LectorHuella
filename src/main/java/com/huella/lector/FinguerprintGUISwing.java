package com.huella.lector;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Window.Type;
import java.awt.Canvas;
import java.awt.BorderLayout;

public class FinguerprintGUISwing extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FinguerprintGUISwing frame = new FinguerprintGUISwing();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FinguerprintGUISwing() {
		setType(Type.UTILITY);
		setTitle("VERIFICACION DE USUARIO");
		setResizable(false);
		getContentPane().setBackground(new Color(192, 192, 192));
		
		Canvas canvas = new Canvas();
		canvas.setEnabled(false);
		canvas.setBackground(new Color(255, 255, 255));
		getContentPane().add(canvas, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 499, 550);
	}

}

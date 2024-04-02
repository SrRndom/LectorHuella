package com.huella.lector;

import javax.swing.*;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;

import java.awt.*;

public class FingerprintGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private FingerprintMultiManager fingerprintManager;

    // Componentes de la interfaz gráfica
    private JTextField userIdField;
    private JTextField userNameField;
    private JTextArea logArea;
    private JButton enrollButton;
    private JButton verifyButton;
    private JButton directValidationButton;
    private JButton multipleEnrollmentButton;

    public FingerprintGUI() {
        super("Gestor de Huellas Digitales");
        try {
            fingerprintManager = new FingerprintMultiManager();
            fingerprintManager.initializeReader();
        } catch (UareUException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inicializando el lector de huellas.");
            System.exit(1);
        }
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        userIdField = new JTextField(10);
        userNameField = new JTextField(10);
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        new JScrollPane(logArea);

        enrollButton = new JButton("Enrolar Huella");
        verifyButton = new JButton("Verificar Huella");
        directValidationButton = new JButton("Validación Directa");
        multipleEnrollmentButton = new JButton("Enrolamiento Múltiple");

        enrollButton.addActionListener(e -> enrollFingerprint());
        verifyButton.addActionListener(e -> verifyFingerprint());
        directValidationButton.addActionListener(e -> directValidation());
        multipleEnrollmentButton.addActionListener(e -> multipleEnrollment());
    }

    private void layoutComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(new JLabel("ID de Usuario:"));
        inputPanel.add(userIdField);
        inputPanel.add(new JLabel("Nombre de Usuario:"));
        inputPanel.add(userNameField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(enrollButton);
        buttonPanel.add(verifyButton);
        buttonPanel.add(directValidationButton);
        buttonPanel.add(multipleEnrollmentButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(inputPanel, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(logArea), BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void enrollFingerprint() {
        int userId = Integer.parseInt(userIdField.getText());
        String userName = userNameField.getText();
        executeInBackground(() -> {
            try {
                fingerprintManager.captureAndEnrollFingerprint();
                fingerprintManager.saveFingerprintToFMD(fingerprintManager.getEnrollmentFmd(), userId, userName, userName);
                SwingUtilities.invokeLater(() -> logArea.append("Huella enrolada con éxito.\n"));
            } catch (UareUException e) {
                SwingUtilities.invokeLater(() -> logArea.append("Error enrolando huella: " + e.getMessage() + "\n"));
            }
        });
    }

    private void verifyFingerprint() {
        Integer.parseInt(userIdField.getText());
        executeInBackground(() -> {
            try {
                boolean verified = fingerprintManager.verifyFingerprint();
                SwingUtilities.invokeLater(() -> logArea.append("Resultado de la verificación: " + verified + "\n"));
            } catch (UareUException e) {
                SwingUtilities.invokeLater(() -> logArea.append("Error en la verificación: " + e.getMessage() + "\n"));
            }
        });
    }

    private void directValidation() {
        executeInBackground(() -> {
            try {
                Fmd[] allFmds = fingerprintManager.retrieveAllFingerprints();
                fingerprintManager.identifyFingerprint(allFmds);
                SwingUtilities.invokeLater(() -> logArea.append("Identificación completada.\n"));
            } catch (UareUException e) {
                SwingUtilities.invokeLater(() -> logArea.append("Error en la identificación de huella: " + e.getMessage() + "\n"));
            }
        });
    }

    private void multipleEnrollment() {
        int userId = Integer.parseInt(userIdField.getText());
        String userName = userNameField.getText();
        executeInBackground(() -> {
            try {
                fingerprintManager.enrollMultipleFingerprints(userId, userName);
                SwingUtilities.invokeLater(() -> logArea.append("Enrolamiento múltiple completado.\n"));
            } catch (UareUException e) {
                SwingUtilities.invokeLater(() -> logArea.append("Error en el enrolamiento múltiple: " + e.getMessage() + "\n"));
            }
        });
    }

    private void executeInBackground(Runnable task) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                task.run();
                return null;
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FingerprintGUI().setVisible(true));
    }
}


package com.royll.xmlformat.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.royll.xmlformat.ConvertBridge;
import com.royll.xmlformat.config.Config;
import com.royll.xmlformat.util.PsiClassUtil;
import com.royll.xmlformat.util.XmlUtils;
import org.apache.http.util.TextUtils;
import org.dom4j.DocumentException;
import org.xml.sax.SAXParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class XmlDialog extends JFrame implements ConvertBridge.Operator {

    private CardLayout cardLayout;
    private JPanel contentPane2;
    private JButton okButton;
    private JButton cancelButton;
    private JLabel errorLB;
    private JTextPane editTP;
    private JLabel generateClassLB;
    private JTextField generateClassTF;
    private JPanel generateClassP;
    private JButton formatBtn;
    private PsiClass cls;
    private PsiFile file;
    private Project project;
    private String errorInfo = null;
    private String currentClass = null;

    public XmlDialog(PsiClass cls, PsiFile file, Project project) throws HeadlessException {
        this.cls = cls;
        this.file = file;
        this.project = project;
        setContentPane(contentPane2);
        setTitle("SimpleXmlFormat");
        getRootPane().setDefaultButton(okButton);
        this.setAlwaysOnTop(true);
        initGeneratePanel(file);
        initListener();
    }

    private void initListener() {

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (generateClassTF.isFocusOwner()) {
                    editTP.requestFocus(true);
                } else {
                    onOK();
                }
            }
        });
        formatBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String xml = editTP.getText().trim();
                    String formatXml = XmlUtils.formatXml(xml);
                    System.out.println("formatXml  " + formatXml);
                    editTP.setText(formatXml);
                    if (errorLB.getText().toString() != null && !errorLB.getText().toString().equals("")) {
                        errorLB.setText("");
                    }
                } catch (SAXParseException saxEx) {
                    errorLB.setText(saxEx.getMessage());
                } catch (DocumentException docException) {
                    errorLB.setText("DocumentException，请检查文档结构");
                } catch (IOException ioException) {
                    errorLB.setText("文件读写异常");
                }
            }
        });
        editTP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                super.keyReleased(keyEvent);
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {

                    onOK();
                }
            }
        });
        generateClassP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                super.keyReleased(keyEvent);
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    editTP.requestFocus(true);
                }
            }
        });
        errorLB.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
//                if (errorInfo != null) {
//                    ErrorDialog errorDialog = new ErrorDialog(errorInfo);
//                    errorDialog.setSize(800, 600);
//                    errorDialog.setLocationRelativeTo(null);
//                    errorDialog.setVisible(true);
//                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane2.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initGeneratePanel(PsiFile file) {

        cardLayout = (CardLayout) generateClassP.getLayout();
        generateClassTF.setBackground(errorLB.getBackground());
        currentClass = ((PsiJavaFileImpl) file).getPackageName() + "." + file.getName().split("\\.")[0];
        generateClassLB.setText(currentClass);
        generateClassTF.setText(currentClass);
        formatBtn.setVisible(true);
        generateClassTF.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                cardLayout.next(generateClassP);
                if (TextUtils.isEmpty(generateClassTF.getText())) {
                    generateClassLB.setText(currentClass);
                    generateClassTF.setText(currentClass);
                } else {
                    generateClassLB.setText(generateClassTF.getText());
                }
            }
        });

        generateClassLB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
//                cardLayout.next(generateClassP);
//                if (generateClassLB.getText().equals(currentClass) && !TextUtils.isEmpty(Config.getInstant().getEntityPackName()) && !Config.getInstant().getEntityPackName().equals("null")) {
//                    generateClassLB.setText(Config.getInstant().getEntityPackName());
//                    generateClassTF.setText(Config.getInstant().getEntityPackName());
//                }
//                generateClassTF.requestFocus(true);
            }

        });
    }

    private void onOK() {

        this.setAlwaysOnTop(false);
        String xmlSTR = editTP.getText().trim();
        if (TextUtils.isEmpty(xmlSTR)) {
            return;
        }
        String generateClassName = generateClassTF.getText().replaceAll(" ", "").replaceAll(".java$", "");
        System.out.println("generateClassName :" + generateClassName);
        if (TextUtils.isEmpty(generateClassName) || generateClassName.endsWith(".")) {
            Toast.make(project, generateClassP, MessageType.ERROR, "the path is not allowed");
            return;
        }
        PsiClass generateClass = null;
        if (!currentClass.equals(generateClassName)) {
            generateClass = PsiClassUtil.exist(file, generateClassTF.getText());
        } else {
            generateClass = cls;
        }

        new ConvertBridge(this, xmlSTR, file, project, generateClass,
                cls, generateClassName).run();
    }

    private void onCancel() {
        dispose();
    }


    public PsiClass getClss() {
        return cls;
    }

    public void setClass(PsiClass mClass) {
        this.cls = mClass;
    }

    public void setProject(Project mProject) {
        this.project = mProject;
    }

    public void setFile(PsiFile mFile) {
        this.file = mFile;
    }

    private void createUIComponents() {

    }

    public void cleanErrorInfo() {
        errorInfo = null;
    }

    public void setErrorInfo(String error) {
        errorInfo = error;
    }

    @Override
    public void showError(ConvertBridge.Error err) {
        switch (err) {
            case DATA_ERROR:
                errorLB.setText("data err !!");
                if (Config.getInstant().isToastError()) {
                    Toast.make(project, errorLB, MessageType.ERROR, "click to see details");
                }
                break;
            case PARSE_ERROR:
                errorLB.setText("parse err !!");
                if (Config.getInstant().isToastError()) {
                    Toast.make(project, errorLB, MessageType.ERROR, "click to see details");
                }
                break;
            case PATH_ERROR:
                Toast.make(project, generateClassP, MessageType.ERROR, "the path is not allowed");
                break;
        }
    }

    @Override
    public void showError(ConvertBridge.Error err, String str) {
        errorLB.setText("error:" + str);
    }
}

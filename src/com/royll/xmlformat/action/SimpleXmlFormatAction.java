package com.royll.xmlformat.action;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.royll.xmlformat.ui.XmlDialog;

/**
 * Created by Dell on 2016/11/17.
 */
public class SimpleXmlFormatAction extends BaseGenerateAction {

    public SimpleXmlFormatAction(){
        super(null);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile mFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass psiClass = getTargetClass(editor, mFile);
        XmlDialog jsonD = new XmlDialog(psiClass, mFile, project);
        jsonD.setClass(psiClass);
        jsonD.setFile(mFile);
        jsonD.setProject(project);
        jsonD.setSize(600, 400);
        jsonD.setLocationRelativeTo(null);
        jsonD.setVisible(true);
    }

}

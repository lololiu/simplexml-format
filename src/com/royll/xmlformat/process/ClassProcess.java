package com.royll.xmlformat.process;

import com.intellij.psi.*;
import com.royll.xmlformat.entity.ClassEntity;


/**
 * Created by dim on 16/11/7.
 */
public class ClassProcess {

    private PsiElementFactory factory;
    private PsiClass cls;
    private Processor processor;

    public ClassProcess(PsiElementFactory factory, PsiClass cls) {
        this.factory = factory;
        this.cls = cls;
        processor = Processor.getProcessor();
    }

    public void generate(ClassEntity classEntity, IProcessor visitor) {
        processor.process(classEntity, factory, cls, visitor);
    }
}

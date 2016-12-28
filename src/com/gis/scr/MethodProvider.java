package com.gis.scr;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;


public interface MethodProvider {

    boolean validate(PsiAnnotation psiAnnotation);

    String getMethodName(PsiField psiField);
}

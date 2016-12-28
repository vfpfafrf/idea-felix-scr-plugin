package com.gis.scr.methods;

import com.gis.scr.MethodProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import de.plushnikov.intellij.plugin.util.PsiAnnotationUtil;

import java.util.Collection;

/**
 *
 */
public class UnbindMethodNameProvider implements MethodProvider {

    @Override
    public boolean validate(PsiAnnotation psiAnnotation) {
        Collection<String> unbind = PsiAnnotationUtil.getAnnotationValues(psiAnnotation, "unbind", String.class);
        return unbind.isEmpty() || unbind.size() ==  1 && unbind.contains("");
    }

    @Override
    public String getMethodName(PsiField psiField) {
        String psiFieldName = psiField.getName();
        return psiFieldName == null ? null : "unbind"+ StringUtil.capitalize(psiFieldName);
    }
}

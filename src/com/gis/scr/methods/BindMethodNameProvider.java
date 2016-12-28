package com.gis.scr.methods;

import com.gis.scr.MethodProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import de.plushnikov.intellij.plugin.util.PsiAnnotationUtil;

import java.util.Collection;

/**
 * Created by dmitry on 12/26/16.
 */
public class BindMethodNameProvider implements MethodProvider {

    @Override
    public boolean validate(PsiAnnotation psiAnnotation) {
        Collection<String> bind = PsiAnnotationUtil.getAnnotationValues(psiAnnotation, "bind", String.class);
        return bind.isEmpty() || bind.size() ==  1 && bind.contains("");
    }

    @Override
    public String getMethodName(PsiField psiField) {
        String psiFieldName = psiField.getName();
        return psiFieldName == null ? null : "bind"+ StringUtil.capitalize(psiFieldName);
    }
}

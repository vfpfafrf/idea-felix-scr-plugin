package com.gis.scr;

import com.gis.scr.methods.BindMethodNameProvider;
import com.gis.scr.methods.UnbindMethodNameProvider;
import com.gis.scr.psi.JavaMethodBuilder;
import com.intellij.psi.*;
import de.plushnikov.intellij.plugin.util.PsiAnnotationSearchUtil;
import de.plushnikov.intellij.plugin.util.PsiClassUtil;
import org.apache.felix.scr.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ReferenceProcessor implements Processor {

    private MethodProvider bindMethodProvider;
    private MethodProvider unbindMethodProvider;

    public ReferenceProcessor() {
        this.bindMethodProvider = new BindMethodNameProvider();
        this.unbindMethodProvider = new UnbindMethodNameProvider();
    }

    @Override
    public List<? super PsiElement> process(PsiClass psiClass) {
        List<? super PsiElement> result = new ArrayList<>();
        for (PsiField psiField : PsiClassUtil.collectClassFieldsIntern(psiClass)) {
            PsiAnnotation psiAnnotation = PsiAnnotationSearchUtil.findAnnotation(psiField, Reference.class);
            if (psiAnnotation != null) {
                if (bindMethodProvider.validate(psiAnnotation)) {
                    result.add(createMethod(psiField, psiClass, bindMethodProvider));
                }
                if (unbindMethodProvider.validate(psiAnnotation)) {
                    result.add(createMethod(psiField, psiClass, unbindMethodProvider));
                }
            }
        }
        return result;
    }

    private PsiElement createMethod(PsiField psiField, PsiClass psiClass, MethodProvider provider) {
        return new JavaMethodBuilder(psiField.getManager(), provider.getMethodName(psiField))
                .withContainingClass(psiClass)
                .withNavigationElement(psiField)
                .withParameter(psiField.getName(), psiField.getType())
                .withMethodReturnType(PsiType.VOID);
    }

}

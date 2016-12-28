package com.gis.scr.psi;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightMethodBuilder;
import org.jetbrains.annotations.NotNull;

/**
 */
public class JavaMethodBuilder extends LightMethodBuilder {

    public JavaMethodBuilder(PsiManager manager, String name) {
        super(manager, JavaLanguage.INSTANCE, name);
    }


    public JavaMethodBuilder withNavigationElement(PsiElement navigationElement) {
        setNavigationElement(navigationElement);
        return this;
    }

    public JavaMethodBuilder withMethodReturnType(PsiType returnType) {
        setMethodReturnType(returnType);
        return this;
    }

    public JavaMethodBuilder withContainingClass(@NotNull PsiClass containingClass) {
        setContainingClass(containingClass);
        return this;
    }

    public JavaMethodBuilder withParameter(String name, PsiType type) {
        addParameter(new JavaParameter(name, type, this));
        return this;
    }
}

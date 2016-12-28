package com.gis.scr.psi;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.light.LightParameter;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class JavaParameter extends LightParameter {

    public JavaParameter(@NotNull String name, @NotNull PsiType type, @NotNull PsiElement declarationScope) {
        super(name, type, declarationScope, JavaLanguage.INSTANCE);
    }
}

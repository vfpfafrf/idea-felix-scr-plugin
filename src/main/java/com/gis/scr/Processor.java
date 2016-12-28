package com.gis.scr;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Created by dmitry on 12/26/16.
 */
public interface Processor<T extends PsiElement> {

    List<T> process(PsiClass psiClass);
}

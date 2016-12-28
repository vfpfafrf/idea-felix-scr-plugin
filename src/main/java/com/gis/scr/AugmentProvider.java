package com.gis.scr;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.augment.PsiAugmentProvider;
import com.intellij.psi.impl.source.PsiExtensibleClass;
import com.intellij.psi.util.CachedValuesManager;
import de.plushnikov.intellij.plugin.util.PsiAnnotationSearchUtil;
import org.apache.felix.scr.annotations.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class AugmentProvider extends PsiAugmentProvider {

    @NotNull
    @Override
    protected <T extends PsiElement> List<T> getAugments(@NotNull PsiElement element, @NotNull Class<T> type) {
        List<T> emptyResult = Collections.emptyList();

        // skip processing during index rebuild
        Project project = element.getProject();
        if (DumbService.isDumb(project)) {
            return emptyResult;
        }
        // Expecting that we are only augmenting an PsiClass
        // Don't filter !isPhysical elements or code auto completion will not work
        if (!(element instanceof PsiExtensibleClass) || !element.isValid()) {
            return emptyResult;
        }
        // Skip processing of Annotations and Interfaces
        if (((PsiClass) element).isAnnotationType() || ((PsiClass) element).isInterface()) {
            return emptyResult;
        }

        if (type == PsiMethod.class) {
            PsiClass psiClass = (PsiClass) element;
            PsiAnnotation annotation = PsiAnnotationSearchUtil.findAnnotation(psiClass, Component.class);
            if (annotation != null) {
                return CachedValuesManager.getCachedValue(element, new BindCachedValueProvider<>(psiClass));
            }
        }
        return emptyResult;
    }
}

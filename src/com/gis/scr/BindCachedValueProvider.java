package com.gis.scr;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 */
public class BindCachedValueProvider<T extends PsiElement> implements CachedValueProvider<List<T>> {

    private final PsiClass psiClass;

    public BindCachedValueProvider(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    @Nullable
    @Override
    public Result<List<T>> compute() {
        Processor<T> processor = new ReferenceProcessor();
        List<T> result = processor.process(psiClass);
        return new Result<>(result, PsiModificationTracker.JAVA_STRUCTURE_MODIFICATION_COUNT);
    }
}

/*
  Copyright (c) 2011-2016, Michail Plushnikov
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or
 other materials provided with the distribution.

 Neither the name of the {organization} nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.plushnikov.intellij.plugin.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Some util methods for annotation processing
 *
 * @author peichhorn
 * @author Plushnikov Michail
 */
public class PsiAnnotationUtil {

  @NotNull
  public static PsiAnnotation createPsiAnnotation(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull Class<? extends Annotation> annotationClass) {
    return createPsiAnnotation(psiModifierListOwner, annotationClass, "");
  }

  @NotNull
  public static PsiAnnotation createPsiAnnotation(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull Class<? extends Annotation> annotationClass, @Nullable String value) {
    final PsiElementFactory elementFactory = JavaPsiFacade.getInstance(psiModifierListOwner.getProject()).getElementFactory();
    final PsiClass psiClass = PsiTreeUtil.getParentOfType(psiModifierListOwner, PsiClass.class);
    final String valueString = StringUtil.isNotEmpty(value) ? "(" + value + ")" : "";
    return elementFactory.createAnnotationFromText("@" + annotationClass.getName() + valueString, psiClass);
  }

  @NotNull
  public static <T> Collection<T> getAnnotationValues(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter, @NotNull Class<T> asClass) {
    Collection<T> result = Collections.emptyList();
    PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue(parameter);
    if (attributeValue instanceof PsiArrayInitializerMemberValue) {
      final PsiAnnotationMemberValue[] memberValues = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
      result = new ArrayList<T>(memberValues.length);

      for (PsiAnnotationMemberValue memberValue : memberValues) {
        T value = resolveElementValue(memberValue, asClass);
        if (null != value) {
          result.add(value);
        }
      }
    } else if (null != attributeValue) {
      T value = resolveElementValue(attributeValue, asClass);
      if (null != value) {
        result = Collections.singletonList(value);
      }
    }
    return result;
  }

  public static boolean getBooleanAnnotationValue(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter, boolean defaultValue) {
    PsiAnnotationMemberValue attrValue = psiAnnotation.findAttributeValue(parameter);
    final Boolean result = null != attrValue ? resolveElementValue(attrValue, Boolean.class) : null;
    return result == null ? defaultValue : result;
  }

  public static String getStringAnnotationValue(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter) {
    PsiAnnotationMemberValue attrValue = psiAnnotation.findAttributeValue(parameter);
    return null != attrValue ? resolveElementValue(attrValue, String.class) : null;
  }

  @Nullable
  private static <T> T resolveElementValue(@NotNull PsiElement psiElement, @NotNull Class<T> asClass) {
    T value = null;
    if (psiElement instanceof PsiReferenceExpression) {
      final PsiElement resolved = ((PsiReferenceExpression) psiElement).resolve();

      if (resolved instanceof PsiEnumConstant) {
        final PsiEnumConstant psiEnumConstant = (PsiEnumConstant) resolved;
        //Enums are supported as VALUE-Strings only
        if (asClass.isAssignableFrom(String.class)) {
          value = (T) psiEnumConstant.getName();
        }
      } else if (resolved instanceof PsiVariable) {
        final PsiVariable psiVariable = (PsiVariable) resolved;
        Object elementValue = psiVariable.computeConstantValue();
        if (null != elementValue && asClass.isAssignableFrom(elementValue.getClass())) {
          value = (T) elementValue;
        }
      }
    } else if (psiElement instanceof PsiLiteralExpression) {
      Object elementValue = ((PsiLiteralExpression) psiElement).getValue();
      if (null != elementValue && asClass.isAssignableFrom(elementValue.getClass())) {
        value = (T) elementValue;
      }
    } else if (psiElement instanceof PsiClassObjectAccessExpression) {
      PsiTypeElement elementValue = ((PsiClassObjectAccessExpression) psiElement).getOperand();
      //Enums are supported as VALUE-Strings only
      if (asClass.isAssignableFrom(PsiType.class)) {
        value = (T) elementValue.getType();
      }
    } else if (psiElement instanceof PsiAnnotation) {
      if (asClass.isAssignableFrom(PsiAnnotation.class)) {
        value = (T) psiElement;
      }
    }
    return value;
  }

  @Nullable
  public static Boolean getDeclaredBooleanAnnotationValue(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter) {
    final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(psiAnnotation.getProject());
    PsiAnnotationMemberValue attributeValue = psiAnnotation.findDeclaredAttributeValue(parameter);
    Object constValue = javaPsiFacade.getConstantEvaluationHelper().computeConstantExpression(attributeValue);
    return constValue instanceof Boolean ? (Boolean) constValue : null;
  }

  @NotNull
  public static Collection<String> collectAnnotationsToCopy(@NotNull PsiField psiField, final Pattern... patterns) {
    Collection<String> annotationsToCopy = new ArrayList<String>();
    PsiModifierList modifierList = psiField.getModifierList();
    if (null != modifierList) {
      for (PsiAnnotation psiAnnotation : modifierList.getAnnotations()) {
        final String annotationName = PsiAnnotationSearchUtil.getSimpleNameOf(psiAnnotation);
        for (Pattern pattern : patterns) {
          if (pattern.matcher(annotationName).matches()) {
            annotationsToCopy.add(psiAnnotation.getQualifiedName());
          }
        }
      }
    }
    return annotationsToCopy;
  }

}

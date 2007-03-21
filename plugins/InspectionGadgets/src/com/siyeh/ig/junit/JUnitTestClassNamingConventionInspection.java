/*
 * Copyright 2003-2007 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.junit;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiTypeParameter;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.fixes.RenameFix;
import com.siyeh.ig.naming.ConventionInspection;
import com.siyeh.ig.psiutils.ClassUtils;
import org.jetbrains.annotations.NotNull;

public class JUnitTestClassNamingConventionInspection
        extends ConventionInspection {

    private static final int DEFAULT_MIN_LENGTH = 8;
    private static final int DEFAULT_MAX_LENGTH = 64;

    @NotNull
    public String getDisplayName() {
        return InspectionGadgetsBundle.message(
                "j.unit.test.class.naming.convention.display.name");
    }

    protected InspectionGadgetsFix buildFix(PsiElement location) {
        return new RenameFix();
    }

    protected boolean buildQuickFixesOnlyForOnTheFlyErrors() {
        return true;
    }

    @NotNull
    public String buildErrorString(Object... infos) {
        final String className = (String)infos[0];
        if (className.length() < getMinLength()) {
            return InspectionGadgetsBundle.message(
                    "j.unit.test.class.naming.convention.problem.descriptor.short");
        } else if (className.length() > getMaxLength()) {
            return InspectionGadgetsBundle.message(
                    "j.unit.test.class.naming.convention.problem.descriptor.long");
        }
        return InspectionGadgetsBundle.message(
                "j.unit.test.class.naming.convention.problem.descriptor.regex.mismatch",
                getRegex());
    }

    protected String getDefaultRegex() {
        return "[A-Z][A-Za-z\\d]*Test";
    }

    protected int getDefaultMinLength() {
        return DEFAULT_MIN_LENGTH;
    }

    protected int getDefaultMaxLength() {
        return DEFAULT_MAX_LENGTH;
    }

    public BaseInspectionVisitor buildVisitor() {
        return new NamingConventionsVisitor();
    }

    private class NamingConventionsVisitor extends BaseInspectionVisitor {

        public void visitClass(@NotNull PsiClass aClass) {
            if (aClass.isInterface() || aClass.isEnum() ||
                    aClass.isAnnotationType()) {
                return;
            }
            if(aClass instanceof PsiTypeParameter) {
                return;
            }
            if(aClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
                return;
            }
            if(!ClassUtils.isSubclass(aClass, "junit.framework.TestCase")) {
                return;
            }
            final String name = aClass.getName();
            if (name == null) {
                return;
            }
            if (isValid(name)) {
                return;
            }
            registerClassError(aClass, name);
        }
    }
}
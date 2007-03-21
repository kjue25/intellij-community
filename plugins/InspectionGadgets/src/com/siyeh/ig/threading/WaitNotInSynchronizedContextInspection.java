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
package com.siyeh.ig.threading;

import com.intellij.psi.*;
import com.siyeh.HardcodedMethodConstants;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.psiutils.SynchronizationUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class WaitNotInSynchronizedContextInspection
        extends BaseInspection {

    @NotNull
    public String getID() {
        return "WaitWhileNotSynced";
    }

    @NotNull
    public String getDisplayName() {
        return InspectionGadgetsBundle.message(
                "wait.not.in.synchronized.context.display.name");
    }

    @NotNull
    protected String buildErrorString(Object... infos) {
        return InspectionGadgetsBundle.message(
                "wait.not.in.synchronized.context.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new WaitNotInSynchronizedContextVisitor();
    }

    private static class WaitNotInSynchronizedContextVisitor
            extends BaseInspectionVisitor {

        public void visitMethodCallExpression(
                @NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression =
                    expression.getMethodExpression();
            @NonNls final String methodName =
                    methodExpression.getReferenceName();
            if (!HardcodedMethodConstants.WAIT.equals(methodName)) {
                return;
            }
            final PsiMethod method = expression.resolveMethod();
            if (method == null) {
                return;
            }
            final PsiParameterList paramList = method.getParameterList();
            final PsiParameter[] parameters = paramList.getParameters();
            final int numParams = parameters.length;
            if (numParams > 2) {
                return;
            }
            if (numParams > 0) {
                final PsiType parameterType = parameters[0].getType();
                if (!parameterType.equals(PsiType.LONG)) {
                    return;
                }
            }
            if (numParams > 1) {
                final PsiType parameterType = parameters[1].getType();
                if (!parameterType.equals(PsiType.INT)) {
                    return;
                }
            }
            if (SynchronizationUtil.isInSynchronizedContext(expression)) {
                return;
            }
            registerMethodCallError(expression);
        }
    }
}
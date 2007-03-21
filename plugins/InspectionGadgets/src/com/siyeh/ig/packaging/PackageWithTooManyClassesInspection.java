/*
 * Copyright 2006-2007 Dave Griffith
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
package com.siyeh.ig.packaging;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.CommonProblemDescriptor;
import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.reference.RefClass;
import com.intellij.codeInspection.reference.RefEntity;
import com.intellij.codeInspection.reference.RefPackage;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseGlobalInspection;
import com.siyeh.ig.ui.SingleIntegerFieldOptionsPanel;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class PackageWithTooManyClassesInspection extends BaseGlobalInspection {

    @SuppressWarnings({"PublicField"})
    public int limit = 10;

    @NotNull
    public String getGroupDisplayName() {
        return GroupNames.PACKAGING_GROUP_NAME;
    }

    @Nullable
    public CommonProblemDescriptor[] checkElement(
            RefEntity refEntity,
            AnalysisScope analysisScope,
            InspectionManager inspectionManager,
            GlobalInspectionContext globalInspectionContext) {
        if (!(refEntity instanceof RefPackage)) {
            return null;
        }
        final RefPackage refPackage = (RefPackage) refEntity;
        int numClasses = 0;
        final List<RefEntity> children = refPackage.getChildren();
        for (RefEntity child : children) {
            if(child instanceof RefClass) {
                numClasses++;
            }
        }
        if(numClasses<=limit) {
            return null;
        }
        final String errorString = InspectionGadgetsBundle.message(
                "package.with.too.many.classes.problem.descriptor",
                refPackage.getQualifiedName(), numClasses, limit);
        return new CommonProblemDescriptor[]{
                inspectionManager.createProblemDescriptor(errorString)
        };
    }

    public JComponent createOptionsPanel() {
        return new SingleIntegerFieldOptionsPanel(
                InspectionGadgetsBundle.message(
                        "package.with.too.many.classes.max.option"),
                this, "limit");
    }

    public boolean isGraphNeeded() {
        return false;
    }
}
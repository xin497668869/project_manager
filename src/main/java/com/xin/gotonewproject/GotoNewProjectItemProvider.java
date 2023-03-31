// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.xin.gotonewproject;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.gotoByName.ChooseByNameViewModel;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.xin.gotonewproject.MyStartupActivity.PROJECT_OPEN_HISTORY_LIST;

/**
 * @author peter
 */
public class GotoNewProjectItemProvider extends DefaultChooseByNameItemProvider {
    private final Project myProject;
    private final GotoNewProjectModel myModel;

    public GotoNewProjectItemProvider(@NotNull Project project, @Nullable PsiElement context, GotoNewProjectModel model) {
        super(context);
        myProject = project;
        myModel = model;

    }

    @Override
    public boolean filterElements(@NotNull ChooseByNameViewModel base,
                                  @NotNull String pattern,
                                  boolean everywhere,
                                  @NotNull ProgressIndicator indicator,
                                  @NotNull Processor<Object> consumer) {
        if (pattern.contains("/") || pattern.contains("\\")) {
            MinusculeMatcher minusculeMatcher = NameUtil.buildMatcher("*" + pattern + "*", NameUtil.MatchingCaseSensitivity.NONE);
            GotoNewProjectItemNavigate gotoNewProjectItemNavigate = new GotoNewProjectItemNavigate("openDir``" + pattern, minusculeMatcher);
            if (gotoNewProjectItemNavigate.getProjectFile()
                    .exists()) {
                if (!consumer.process(gotoNewProjectItemNavigate)) {
                    return true;
                }
            }
        }
        String[] values = PropertiesComponent.getInstance()
                                             .getValues(PROJECT_OPEN_HISTORY_LIST);
        if (values == null) {
            return true;
        }
        MinusculeMatcher minusculeMatcher = NameUtil.buildMatcher("*" + pattern + "*", NameUtil.MatchingCaseSensitivity.NONE);
        for (int i = 0; i < values.length; i++) {
            String projectPath = values[i];

            if (minusculeMatcher.matches(projectPath)) {
                GotoNewProjectItemNavigate gotoNewProjectItemNavigate = new GotoNewProjectItemNavigate(projectPath, minusculeMatcher);
                if (gotoNewProjectItemNavigate.getProjectFile()
                                              .exists()) {
                    if (!consumer.process(gotoNewProjectItemNavigate)) {
                        return true;
                    }
                }
            }
        }
        return true;
    }

}

// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.xin.gotoproject;

import com.intellij.ide.util.gotoByName.ChooseByNameBase;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author peter
 */
public class GotoProjectItemProvider extends DefaultChooseByNameItemProvider {
    private final Project myProject;

    public GotoProjectItemProvider(@NotNull Project project, @Nullable PsiElement context, GotoProjectModel model) {
        super(context);
        myProject = project;

    }

    @Override
    public boolean filterElements(@NotNull ChooseByNameBase base,
                                  @NotNull String pattern,
                                  boolean everywhere,
                                  @NotNull ProgressIndicator indicator,
                                  @NotNull Processor<Object> consumer) {

        IdeFrame[] allProjectFrames = WindowManager.getInstance()
                                                   .getAllProjectFrames();
        MinusculeMatcher minusculeMatcher = NameUtil.buildMatcher("*" + pattern + "*", NameUtil.MatchingCaseSensitivity.NONE);
        ProjectNavigate activeProjectNavigate = null;
        for (IdeFrame ideFrame : allProjectFrames) {
            if (ideFrame.getProject() != null) {
                if (minusculeMatcher.matches(ideFrame.getProject()
                                                     .getName())) {
                    ProjectNavigate projectNavigate = new ProjectNavigate(ideFrame.getProject(), minusculeMatcher);
                    if (projectNavigate.getProject()
                                       .equals(myProject)) {
                        activeProjectNavigate = projectNavigate;
                    } else {
                        consumer.process(projectNavigate);
                    }
                }
            }
        }
        if (activeProjectNavigate != null) {
            consumer.process(activeProjectNavigate);
        }
        return true;
    }

}

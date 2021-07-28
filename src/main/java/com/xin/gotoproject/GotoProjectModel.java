// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.xin.gotoproject;

import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Model for "Go to | File" action
 */
public class GotoProjectModel extends FilteringGotoByModel<IdeFrameImpl> implements DumbAware, Comparator<Object> {
    private final int     myMaxSize;
    private       Project project;

    public GotoProjectModel(@NotNull Project project) {
        super(project, Extensions.getExtensions(ChooseByNameContributor.FILE_EP_NAME));
        this.project = project;
        myMaxSize = ApplicationManager.getApplication().isUnitTestMode() ? Integer.MAX_VALUE : WindowManagerEx.getInstanceEx().getFrame(project).getSize().width;
    }

    @Override
    protected boolean acceptItem(final NavigationItem item) {
        return super.acceptItem(item);

    }

    @Nullable
    @Override
    protected IdeFrameImpl filterValueFor(NavigationItem item) {
        return null;
    }

    @Override
    public String getPromptText() {
        return "Enter Project Name:   alt + right can remove selected project";
    }

    @Override
    public String getCheckBoxName() {
        return null;
    }

    @Override
    public char getCheckBoxMnemonic() {
        return SystemInfo.isMac ? 'P' : 'n';
    }

    @Override
    public String getNotInMessage() {
        return "no project found";
    }

    @Override
    public String getNotFoundMessage() {
        return "no project found";
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {
        return;
    }

    @Override
    public PsiElementListCellRenderer getListCellRenderer() {
        return new GotoProjectCellRenderer(myMaxSize, getProject());
    }

    @Override
    public boolean sameNamesForProjectAndLibraries() {
        return !FileBasedIndex.ourEnableTracingOfKeyHashToVirtualFileMapping;
    }

    @Override
    @Nullable
    public String getFullName(final Object element) {
        return element instanceof ProjectNavigate ? ((ProjectNavigate) element).getProject().getName() : "";
    }


    @Override
    @NotNull
    public String[] getSeparators() {
        return new String[]{"/", "\\"};
    }

    @Override
    public String getHelpId() {
        return "";
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @NotNull
    @Override
    public String removeModelSpecificMarkup(@NotNull String pattern) {
        if ((pattern.endsWith("/") || pattern.endsWith("\\"))) {
            return pattern.substring(0, pattern.length() - 1);
        }
        return pattern;
    }

    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
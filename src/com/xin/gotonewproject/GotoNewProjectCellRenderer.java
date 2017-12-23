/*
 * Copyright 2000-2016 JetBrains s.r.o.
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

package com.xin.gotonewproject;

import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ui.FilePathSplittingPolicy;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class GotoNewProjectCellRenderer extends PsiElementListCellRenderer<PsiFileSystemItem> {
    private final int myMaxWidth;
    private final Project project;

    public GotoNewProjectCellRenderer(int maxSize, Project project) {
        myMaxWidth = maxSize;
        this.project = project;
    }

    @Override
    public String getElementText(PsiFileSystemItem element) {
        return element.getName();
    }

    @Override
    protected String getContainerText(PsiFileSystemItem element, String name) {
        PsiFileSystemItem parent = element.getParent();
        final PsiDirectory psiDirectory = parent instanceof PsiDirectory ? (PsiDirectory) parent : null;
        if (psiDirectory == null) return null;
        final VirtualFile virtualFile = psiDirectory.getVirtualFile();
        final String relativePath = getRelativePath(virtualFile, element.getProject());
        if (relativePath == null) return "( " + File.separator + " )";
        String path =
                FilePathSplittingPolicy.SPLIT_BY_SEPARATOR.getOptimalTextForComponent(name + "          ", new File(relativePath), this, myMaxWidth);
        return "(" + path + ")";
    }

    @Nullable
    public static String getRelativePath(final VirtualFile virtualFile, final Project project) {
        if (project == null) {
            return virtualFile.getPresentableUrl();
        }
        VirtualFile root = getAnyRoot(virtualFile, project);
        if (root != null) {
            return getRelativePathFromRoot(virtualFile, root);
        }

        String url = virtualFile.getPresentableUrl();
        final VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null) {
            final String projectHomeUrl = baseDir.getPresentableUrl();
            if (url.startsWith(projectHomeUrl)) {
                final String cont = url.substring(projectHomeUrl.length());
                if (cont.isEmpty()) return null;
                url = "..." + cont;
            }
        }
        return url;
    }

    @Nullable
    public static VirtualFile getAnyRoot(@NotNull VirtualFile virtualFile, @NotNull Project project) {
        ProjectFileIndex index = ProjectFileIndex.SERVICE.getInstance(project);
        VirtualFile root = index.getContentRootForFile(virtualFile);
        if (root == null) root = index.getClassRootForFile(virtualFile);
        if (root == null) root = index.getSourceRootForFile(virtualFile);
        return root;
    }

    @NotNull
    static String getRelativePathFromRoot(@NotNull VirtualFile file, @NotNull VirtualFile root) {
        return root.getName() + File.separatorChar + VfsUtilCore.getRelativePath(file, root, File.separatorChar);
    }

    @Override
    protected boolean customizeNonPsiElementLeftRenderer(ColoredListCellRenderer renderer, JList list, Object value, int index, boolean selected, boolean hasFocus) {
        GotoNewProjectItemNavigate item = (GotoNewProjectItemNavigate) value;
//        Module[] sortedModules = ModuleManager.getInstance(item.getIdeFrame().getProject()).getSortedModules();
//        if (sortedModules.length > 0) {
//        }
        renderer.setIcon(item.getIcon());
        appendColorFrame(renderer, selected, item.getProjectName() + " - " + item.getProjectBasePath(), new SimpleTextAttributes(null, null, null, 0), item.getPattern());
        return true;
    }

    private void appendColorFrame(ColoredListCellRenderer renderer, boolean selected, String name, SimpleTextAttributes attributes, MinusculeMatcher pattern) {
        SpeedSearchUtil.appendColoredFragmentForMatcher(name
                , renderer
                , attributes
                , pattern
                , UIUtil.getListBackground()
                , selected);
    }

    @Override
    protected int getIconFlags() {
        return Iconable.ICON_FLAG_READ_STATUS;
    }
}

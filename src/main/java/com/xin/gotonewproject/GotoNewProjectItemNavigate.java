package com.xin.gotonewproject;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

import static com.xin.gotonewproject.MyStartupActivity.DELIMITER;

/**
 * @author 497668869@qq.com
 */
public class GotoNewProjectItemNavigate implements NavigationItem {

    private String projectBasePath;
    private String projectName;
    private File projectFile;
    private Icon icon;
    private MinusculeMatcher pattern;

    public GotoNewProjectItemNavigate(String projectBasePath, MinusculeMatcher pattern) {
        String[] split = projectBasePath.split(DELIMITER);

        this.projectBasePath = split[1];
        this.projectFile = new File(this.projectBasePath);
        this.projectName = split[0];
        if (split.length > 2 && (split[2] == null ||split[2].isEmpty())) {
            this.icon = AllIcons.Nodes.IdeaModule;
        } else {
            this.icon = ModuleTypeManager.getInstance().findByID(split[1]).getIcon();
        }
        this.pattern = pattern;
    }

    public GotoNewProjectItemNavigate() {
    }

    public String getProjectName() {
        return projectName;
    }

    public File getProjectFile() {
        return projectFile;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getProjectBasePath() {
        return projectBasePath;
    }

    public void setProjectBasePath(String projectBasePath) {
        this.projectBasePath = projectBasePath;
    }

    public MinusculeMatcher getPattern() {
        return pattern;
    }

    public void setPattern(MinusculeMatcher pattern) {
        this.pattern = pattern;
    }

    @Nullable
    @Override
    public String getName() {
        return null;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return null;
    }

    @Override
    public void navigate(boolean requestFocus) {

    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

}

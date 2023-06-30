package com.xin.gotoproject;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import org.jetbrains.annotations.Nullable;

/**
 * @author 497668869@qq.com
 */
public class ProjectNavigate implements NavigationItem {

    private Project project;
    private MinusculeMatcher pattern; 
    
    public ProjectNavigate(Project project, MinusculeMatcher pattern) {
        this.project = project;
        this.pattern = pattern;
    }



    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

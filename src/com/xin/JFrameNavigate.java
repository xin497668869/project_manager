package com.xin;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import org.jetbrains.annotations.Nullable;

/**
 * @author linxixin@cvte.com
 */
public class JFrameNavigate implements NavigationItem {

    private IdeFrameImpl     ideFrame;
    private MinusculeMatcher pattern; 
    
    public JFrameNavigate(IdeFrameImpl ideFrame, MinusculeMatcher pattern) {
        this.ideFrame = ideFrame;
        this.pattern = pattern;
    }

    public JFrameNavigate() {
    }

    public IdeFrameImpl getIdeFrame() {
        return ideFrame;
    }

    public void setIdeFrame(IdeFrameImpl ideFrame) {
        this.ideFrame = ideFrame;
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

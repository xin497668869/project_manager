package com.xin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.wm.IdeFocusManager;

import javax.swing.*;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class EditorFocusAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        JComponent myEditorComponent = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor().getContentComponent();
        if (IdeFocusManager.getGlobalInstance().getFocusOwner() != myEditorComponent) { //IDEA-64501
            IdeFocusManager.getGlobalInstance().requestFocus(myEditorComponent, true);
        }
    }
}

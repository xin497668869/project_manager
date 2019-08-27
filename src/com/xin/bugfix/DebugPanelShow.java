package com.xin.bugfix;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * Debug panel有时焦点不能定位, 特意弄个action可以随时切换焦点
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class DebugPanelShow extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        ToolWindow activateDebugToolWindow =
                ToolWindowManager.getInstance(project).getToolWindow("Debug");
        Component component = activateDebugToolWindow.getComponent().getComponents()[0];
        Component component1 = ((JPanel) ((JPanel) (((JPanel) (((JPanel) component).getComponent(0))).getComponent(0))).getComponent(0)).getComponent(0);

        if (!activateDebugToolWindow.isVisible() || !activateDebugToolWindow.isActive() ) {
            activateDebugToolWindow.activate(() -> {
            });
            component1.requestFocus();
        } else {
            activateDebugToolWindow.hide(() -> {
            });
        }
    }
}

package com.xin.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.BitUtil;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.KeyEvent;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class ActiveUtils {

    /**
     * 页面激活
     */
    public static void active(Project project, AnActionEvent e) {
        if (project != null) {
            JFrame projectFrame = WindowManager.getInstance().getFrame(project);
            if (projectFrame != null) {
                int frameState = projectFrame.getExtendedState();
                if (!SystemInfo.isMac || !BitUtil.isSet(projectFrame.getExtendedState(), 1) || !(e.getInputEvent() instanceof KeyEvent)) {
                    if (BitUtil.isSet(frameState, 1)) {
                        projectFrame.setExtendedState(BitUtil.set(frameState, 1, false));
                    }
                    projectFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

                    projectFrame.toFront();
                    IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {
                        Component mostRecentFocusOwner = projectFrame.getMostRecentFocusOwner();
                        if (mostRecentFocusOwner != null) {
                            IdeFocusManager.getGlobalInstance().requestFocus(mostRecentFocusOwner, true);
                        }

                    });
                }
            }
        }
    }
}


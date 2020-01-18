package com.xin.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.BitUtil;

import javax.swing.*;
import java.awt.Component;
import java.awt.Frame;
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
        JFrame projectFrame = WindowManager.getInstance()
                                           .getFrame(project);
        if (projectFrame == null) {
            return;
        }
        if (SystemInfo.isMac && BitUtil.isSet(projectFrame.getExtendedState(), 1) && e.getInputEvent() instanceof KeyEvent) {
            return;
        }

        int frameState = projectFrame.getExtendedState();
        if (BitUtil.isSet(frameState, Frame.ICONIFIED)) {
            // restore the frame if it is minimized
            projectFrame.setExtendedState(frameState ^ Frame.ICONIFIED);
        }

        projectFrame.toFront();
        projectFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        IdeFocusManager.getGlobalInstance()
                       .doWhenFocusSettlesDown(() -> {
                           Component mostRecentFocusOwner = projectFrame.getMostRecentFocusOwner();
                           if (mostRecentFocusOwner != null) {
                               IdeFocusManager.getGlobalInstance()
                                              .requestFocus(mostRecentFocusOwner, true);
                           }

                       });
    }
}


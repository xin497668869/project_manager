package com.xin.gotonewproject;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

import static com.xin.gotonewproject.MyStartupActivity.DELIMITER;
import static com.xin.gotonewproject.MyStartupActivity.PROJECT_OPEN_HISTORY_WORKSPACE;

/**
 * @author linxixin@cvte.com
 */
public class SearchProjectInfoAction extends AnAction {


    public static class ProjectInfo {
        private String projectName;
        private String projectPath;
        private String icon;
        private long lastModify;

        public ProjectInfo(String projectName, String projectPath, String icon, long lastModify) {
            this.projectName = projectName;
            this.projectPath = projectPath;
            this.icon = icon;
            if (icon == null) {
                this.icon = "";
            }
            this.lastModify = lastModify;
        }

        public String toStoreString() {
            return projectName + DELIMITER + projectPath + DELIMITER + icon;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getProjectPath() {
            return projectPath;
        }

        public String getIcon() {
            return icon;
        }

        public long getLastModify() {
            return lastModify;
        }
    }


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        action(anActionEvent.getProject());
    }

    public static void action(Project project) {
        String workspace = PropertiesComponent.getInstance().getValue(PROJECT_OPEN_HISTORY_WORKSPACE);
        if (workspace == null) {
            workspace = "";
        }

        workspace = Messages.showInputDialog(project, "Workspace Path To Search Project", "Tips", null, workspace, null);
        if (!StringUtils.isEmpty(workspace)) {
            while (true) {
                if (!new File(workspace).exists()) {
                    workspace = Messages.showInputDialog(project, "Wrong Workspaces Path", "Tips", null, "workspace", null);
                    if (StringUtils.isEmpty(workspace)) {
                        return;
                    }
                } else {
                    break;
                }
            }
            ProgressManager.getInstance().run(new ProjectInitTask(project, workspace));
        }
    }
}

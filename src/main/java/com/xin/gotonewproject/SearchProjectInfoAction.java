package com.xin.gotonewproject;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.xin.util.StringUtils;

import java.io.File;

import static com.xin.gotonewproject.MyStartupActivity.DELIMITER;
import static com.xin.gotonewproject.MyStartupActivity.PROJECT_OPEN_HISTORY_WORKSPACE;

/**
 * @author linxixin@cvte.com
 */
public class SearchProjectInfoAction extends AnAction {

    public static void action(Project project) {
        String workspaces = PropertiesComponent.getInstance()
                                               .getValue(PROJECT_OPEN_HISTORY_WORKSPACE);
        if (workspaces == null) {
            workspaces = "";
        }

        workspaces = Messages.showInputDialog(project, "请填写workspace来搜索项目, 多个路径用 ; 分割路径", "Tips", null, workspaces, null);
        if (validateWorkSpace(project, workspaces)) {
            ProgressManager.getInstance()
                           .run(new ProjectInitTask(project, workspaces));
        }
    }

    public static boolean validateWorkSpace(Project project, String workspaces) {
        if (!StringUtils.isEmpty(workspaces)) {
            while (true) {
                for (String workspace : workspaces.split(";")) {
                    if (!new File(workspace).exists()) {
                        workspaces = Messages.showInputDialog(project, "错误的workspace路径", "Tips", null, "workspace", null);
                        if (StringUtils.isEmpty(workspaces)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        action(anActionEvent.getProject());
    }

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
}

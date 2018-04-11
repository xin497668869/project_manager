package com.xin.gotonewproject;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.xin.gotonewproject.MyStartupActivity.PROJECT_OPEN_HISTORY_LIST_MAX;
import static com.xin.gotonewproject.MyStartupActivity.PROJECT_OPEN_HISTORY_WORKSPACE;

/**
 * @author linxixin@cvte.com
 */
public class ProjectInitTask extends Backgroundable {
    private boolean stopScan = true;
    private String  workspace;

    public ProjectInitTask(@Nullable Project project, String workspace) {
        super(project, "scanning workspace", true);
        this.workspace = workspace;
        this.stopScan = false;
    }

    public boolean searchProject(@NotNull File workspace, List<SearchProjectInfoAction.ProjectInfo> projectInfoList) {

        if (stopScan) {
            return true;
        }

        if (workspace.isDirectory()) {
            if (workspace.getName().lastIndexOf(".idea") >= 0) {
                File[] imlFile = new File(workspace.getParent()).listFiles((dir, name) -> name.lastIndexOf(".iml") >= 0);
                if (imlFile != null && imlFile.length > 0) {
                    String projectName = imlFile[0].getName().substring(0, imlFile[0].getName().lastIndexOf(".iml"));
                    File file = new File(workspace, "workspace.xml");
                    long lastModified;
                    if (file.exists()) {
                        lastModified = file.lastModified();
                    } else {
                        lastModified = workspace.lastModified();
                    }
                    SearchProjectInfoAction.ProjectInfo projectInfo = new SearchProjectInfoAction.ProjectInfo(projectName, workspace.getParent().replace("\\", "/"), "", lastModified);
                    projectInfoList.add(projectInfo);
                }
                return true;
            }

            for (File file : workspace.listFiles()) {
                if (workspace.getName().lastIndexOf(".git") < 0) {
                    if (searchProject(file, projectInfoList)) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onCancel() {
        super.onCancel();
        stopScan = true;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        List<SearchProjectInfoAction.ProjectInfo> projectInfos = new ArrayList<>();
        searchProject(new File(workspace), projectInfos);
        PropertiesComponent.getInstance().setValue(PROJECT_OPEN_HISTORY_WORKSPACE, workspace);
        projectInfos.sort(Comparator.comparing(e -> -e.getLastModify()));
        if (projectInfos.size() > PROJECT_OPEN_HISTORY_LIST_MAX) {
            projectInfos = projectInfos.subList(0, PROJECT_OPEN_HISTORY_LIST_MAX);
        }
        for (int i = projectInfos.size() - 1; i >= 0; i--) {
            if (stopScan) {
                break;
            }
            MyStartupActivity.storeProjectInfo(projectInfos.get(i));
        }

    }
}

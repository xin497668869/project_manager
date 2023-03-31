package com.xin.gotonewproject;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author linxixin@cvte.com
 */
public class MyStartupActivity implements StartupActivity, ProjectActivity {

    public static final String PROJECT_OPEN_HISTORY_LIST = "PROJECT_OPEN_HISTORY_LIST";
    public static final String PROJECT_OPEN_HISTORY_WORKSPACE = "PROJECT_OPEN_HISTORY_WORKSPACE";
    public static final int PROJECT_OPEN_HISTORY_LIST_MAX = 1000;
    public static final String DELIMITER = "``";


    @Override
    public void runActivity(@NotNull Project project) {
        if (PropertiesComponent.getInstance().getValue(PROJECT_OPEN_HISTORY_WORKSPACE) == null) {
            storeProjectInfo(new SearchProjectInfoAction.ProjectInfo(project.getName(), project.getBasePath().replace("\\", "/"), getProjectIconId(project), 0));
            PropertiesComponent.getInstance().setValue(PROJECT_OPEN_HISTORY_WORKSPACE,"");
            SearchProjectInfoAction.action(project);
        } else {
            storeProjectInfo(new SearchProjectInfoAction.ProjectInfo(project.getName(), project.getBasePath().replace("\\", "/"), getProjectIconId(project), 0));
        }
    }

    public static void storeProjectInfo(SearchProjectInfoAction.ProjectInfo projectInfo) {
        String[] values = PropertiesComponent.getInstance().getValues(PROJECT_OPEN_HISTORY_LIST);
        if (values == null) {
            values = new String[1];
        }

        if (values.length > PROJECT_OPEN_HISTORY_LIST_MAX) {
            String[] newValues = new String[PROJECT_OPEN_HISTORY_LIST_MAX];
            System.arraycopy(values, 0, newValues, 0, PROJECT_OPEN_HISTORY_LIST_MAX);
            values = newValues;
        }

        int i;
        for (i = 0; i < values.length; i++) {

            if (values[i] == null) {
                break;
            }
            String[] split = values[i].split(DELIMITER);
            if (split[0].equals(projectInfo.getProjectName()) && split[1].equals(projectInfo.getProjectPath().replace("\\", "/"))) {
                break;
            }
        }
        if (i >= values.length && values.length < PROJECT_OPEN_HISTORY_LIST_MAX) {
            String[] newValues = new String[values.length + 1];
            System.arraycopy(values, 0, newValues, 1, values.length);
            values = newValues;
        } else {
            for (int j = i; j > 0; j--) {
                if (j < values.length) {
                    values[j] = values[j - 1];
                }
            }
        }

        values[0] = projectInfo.toStoreString();
        PropertiesComponent.getInstance().setValues(PROJECT_OPEN_HISTORY_LIST, values);
    }

    private String getProjectIconId(Project project) {
        Module[] sortedModules = ModuleManager.getInstance(project).getSortedModules();
        if (sortedModules == null || sortedModules.length < 1) {
            return "";
        }
        return sortedModules[0].getModuleTypeName();
    }

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        return null;
    }
}


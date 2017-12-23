package com.xin.gotonewproject;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * @author linxixin@cvte.com
 */
public class MyStartupActivity implements StartupActivity {

    public static final String PROJECT_OPEN_HISTORY_LIST = "PROJECT_OPEN_HISTORY_LIST";
    public static final Integer PROJECT_OPEN_HISTORY_LIST_MAX = 100;
    public static final String DELIMITER = "||";

    @Override
    public void runActivity(@NotNull Project project) {
        project.getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
            @Override
            public void projectOpened(Project project) {

                String[] values = PropertiesComponent.getInstance().getValues(PROJECT_OPEN_HISTORY_LIST);
                if (values == null) {
                    values = new String[1];
                }

                if (values.length > PROJECT_OPEN_HISTORY_LIST_MAX) {
                    String[] newValues = new String[PROJECT_OPEN_HISTORY_LIST_MAX];
                    System.arraycopy(values, 0, newValues, 0, PROJECT_OPEN_HISTORY_LIST_MAX);
                    values = newValues;
                }

                String storeValue = project.getName() + DELIMITER + getProjectIconId(project) + DELIMITER + project.getBasePath();
                int i;
                for (i = 0; i < values.length; i++) {

                    if (values[i] == null) {
                        break;
                    }
                    if (values[i].equals(storeValue)) {
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

                values[0] = storeValue;
                PropertiesComponent.getInstance().setValues(PROJECT_OPEN_HISTORY_LIST, values);
            }
        });
    }

    private String getProjectIconId(Project project) {
        Module[] sortedModules = ModuleManager.getInstance(project).getSortedModules();
        if (sortedModules == null || sortedModules.length < 1) {
            return "";
        }
        return sortedModules[0].getModuleTypeName();
    }

}
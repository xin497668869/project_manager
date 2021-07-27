package com.xin.gotonewproject;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.xin.util.ActiveUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import static com.intellij.ide.util.gotoByName.ChooseByNamePopup.CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY;

/**
 * @author linxixin@cvte.com
 */
public class GotoNewProjectAction extends GotoActionBase implements DumbAware {
    private final static Logger log = Logger.getInstance(GotoNewProjectAction.class);

    @Override
    public void gotoActionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }

        final GotoNewProjectModel gotoFileModel = new GotoNewProjectModel(project);
        GotoActionCallback<String> callback = new GotoActionCallback<String>() {

            @Override
            public void elementChosen(final ChooseByNamePopup popup, final Object element) {
                log.info("elementChosen " + element + "  " );
                if (element != null) {
                    log.info("class "+element.getClass());
                }

                if (element instanceof GotoNewProjectItemNavigate) {
                    try {
                        String projectBasePath = ((GotoNewProjectItemNavigate) element).getProjectBasePath();

                        boolean exists = new File(projectBasePath).exists();
                        log.info("projectBasePath "+projectBasePath+"  "+exists);

                        if (exists) {
                            IdeFrame[] allProjectFrames = WindowManager.getInstance().getAllProjectFrames();
                            for (IdeFrame ideFrame : allProjectFrames) {
                                if (ideFrame.getProject() != null
                                        && ideFrame.getProject().getName().equals(StringUtils.substringAfterLast(projectBasePath, "/"))){
                                    ActiveUtils.active(ideFrame.getProject(), e);
                                    return;
                                }
                            }
                            ProjectManagerEx.getInstanceEx().loadAndOpenProject(projectBasePath);
                        }
                    } catch (IOException | JDOMException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        boolean mayRequestOpenInCurrentWindow = gotoFileModel.willOpenEditor() && FileEditorManagerEx.getInstanceEx(project)
                                                                                                     .hasSplitOrUndockedWindows();
        Pair<String, Integer> start = getInitialText(true, e);
        ChooseByNamePopup popup = myCreatePopup(project, gotoFileModel,
                                                new GotoNewProjectItemProvider(project, getPsiContext(e), gotoFileModel), start.first,
                                                mayRequestOpenInCurrentWindow,
                                                start.second);

        showNavigationPopup(callback, "open new Project",
                            popup, false);

    }

    private ChooseByNamePopup myCreatePopup(final Project project,
                                            @NotNull final ChooseByNameModel model,
                                            @NotNull ChooseByNameItemProvider provider,
                                            @Nullable final String predefinedText,
                                            boolean mayRequestOpenInCurrentWindow,
                                            final int initialIndex) {
        final ChooseByNamePopup oldPopup = project == null ? null : project.getUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY);
        if (oldPopup != null) {
            oldPopup.close(false);
        }
        ChooseByNamePopup newPopup = new ChooseByNamePopup(project, model, provider, oldPopup, predefinedText,
                                                           mayRequestOpenInCurrentWindow, initialIndex) {
            @Override
            protected void initUI(Callback callback, ModalityState modalityState, boolean allowMultipleSelection) {
                if (myTextField.getText()
                               .isEmpty()) {
                    myTextField.setText("*");
                }
                super.initUI(callback, modalityState, allowMultipleSelection);
            }
        };

        if (project != null) {
            project.putUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup);
        }
        return newPopup;
    }
}

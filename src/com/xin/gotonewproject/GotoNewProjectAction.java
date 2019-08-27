package com.xin.gotonewproject;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.impl.ActionMenuItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.BitUtil;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.intellij.ide.util.gotoByName.ChooseByNamePopup.CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY;


/**
 * @author linxixin@cvte.com
 */
public class GotoNewProjectAction extends GotoActionBase implements DumbAware {
    public static final String ID = "openProject";

    @Override
    public void gotoActionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;

        final GotoNewProjectModel gotoFileModel = new GotoNewProjectModel(project);
        GotoActionCallback<String> callback = new GotoActionCallback<String>() {

            @Override
            public void elementChosen(final ChooseByNamePopup popup, final Object element) {

                if (element instanceof GotoNewProjectItemNavigate) {
                    try {
                        String projectBasePath = ((GotoNewProjectItemNavigate) element).getProjectBasePath();
                        if (new File(projectBasePath).exists()) {
                            Project project1 = ProjectManagerEx.getInstanceEx().loadAndOpenProject(projectBasePath);
                            if (project1.isDisposed()) {
                                for (IdeFrame ideFrame : WindowManager.getInstance().getAllProjectFrames()) {
                                    if (ideFrame.getProject().getBasePath().replace("\\", "/").equals(projectBasePath.replace("\\", "/"))) {
                                        if (active(project1, (JFrame) ideFrame, e)) return;
                                    }
                                }
                            } else {
                                if (active(project1, (JFrame) WindowManager.getInstance().getIdeFrame(project1), e))
                                    return;
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (JDOMException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        boolean mayRequestOpenInCurrentWindow = gotoFileModel.willOpenEditor() && FileEditorManagerEx.getInstanceEx(project).hasSplitOrUndockedWindows();
        Pair<String, Integer> start = getInitialText(true, e);
        ChooseByNamePopup popup = myCreatePopup(project, gotoFileModel, new GotoNewProjectItemProvider(project, getPsiContext(e), gotoFileModel), start.first,
                                                mayRequestOpenInCurrentWindow,
                                                start.second);


        showNavigationPopup(callback, "open new Project",
                            popup, false);

    }

    private boolean active(Project project, JFrame ideFrame, AnActionEvent e) {
        JFrame projectFrame = ideFrame;
        final int frameState = projectFrame.getExtendedState();
        boolean macMainMenu = SystemInfo.isMac && ActionPlaces.isMainMenuOrActionSearch(e.getPlace());
        if (macMainMenu && !(e.getInputEvent().getSource() instanceof ActionMenuItem) && (projectFrame.getExtendedState() & Frame.ICONIFIED) != 0) {
            // On Mac minimized window should not be restored this wa
            projectFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            return true;
        }

        if (BitUtil.isSet(frameState, Frame.ICONIFIED)) {
            // restore the frame if it is minimized
            projectFrame.setExtendedState(frameState ^ Frame.ICONIFIED);
        }
        projectFrame.toFront();
        projectFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(!project.isDisposed()){
                        JComponent myEditorComponent = FileEditorManager.getInstance(project).getSelectedTextEditor().getContentComponent();
                        if (IdeFocusManager.getGlobalInstance().getFocusOwner() != myEditorComponent) { //IDEA-64501
                            IdeFocusManager.getGlobalInstance().requestFocus(myEditorComponent, true);
                        }
                    }
                }
            });
        });
        return false;
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
        ChooseByNamePopup newPopup = new ChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex) {
            @Override
            protected void initUI(Callback callback, ModalityState modalityState, boolean allowMultipleSelection) {
                if (myTextField.getText().isEmpty()) {
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

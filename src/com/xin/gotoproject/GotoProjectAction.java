package com.xin.gotoproject;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectManagerImpl;
import com.intellij.openapi.util.Pair;
import com.xin.util.ActiveUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.intellij.ide.util.gotoByName.ChooseByNamePopup.CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY;

/**
 * @author linxixin@cvte.com
 */
public class GotoProjectAction extends GotoActionBase implements DumbAware {
    public static final String ID = "GotoFile";

    /*

                if (element instanceof JFrameNavigate) {
                    IdeFrameImpl projectFrame = ((JFrameNavigate) element).getIdeFrame();
                    final int frameState = projectFrame.getExtendedState();
                    boolean macMainMenu = SystemInfo.isMac && ActionPlaces.isMainMenuOrActionSearch(e.getPlace());
                    if (macMainMenu && !(e.getInputEvent().getSource() instanceof ActionMenuItem) && (projectFrame.getExtendedState() & Frame.ICONIFIED) != 0) {
                        // On Mac minimized window should not be restored this way
                        return;
                    }

                    if (BitUtil.isSet(frameState, Frame.ICONIFIED)) {
                        // restore the frame if it is minimized
                        projectFrame.setExtendedState(frameState ^ Frame.ICONIFIED);
                    }
                    projectFrame.toFront();

                    IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {
                        IdeFocusManager.getGlobalInstance().requestFocus(((JFrameNavigate) element).getIdeFrame(), true);
                    });
     */
    @Override
    public void gotoActionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;

        final GotoProjectModel gotoFileModel = new GotoProjectModel(project);
        GotoActionBase.GotoActionCallback<Project> callback = new GotoActionCallback<Project>() {

            @Override
            public void elementChosen(final ChooseByNamePopup popup, final Object element) {

                if (element instanceof ProjectNavigate) {
                    ActiveUtils.active(((ProjectNavigate) element).getProject(),e);

                }
            }
        };

        boolean mayRequestOpenInCurrentWindow = gotoFileModel.willOpenEditor() && FileEditorManagerEx.getInstanceEx(project).hasSplitOrUndockedWindows();
        Pair<String, Integer> start = getInitialText(true, e);

        ChooseByNamePopup popup = myCreatePopup(project, gotoFileModel, new GotoProjectItemProvider(project, getPsiContext(e), gotoFileModel), start.first,
                                                mayRequestOpenInCurrentWindow,
                                                start.second);


        showNavigationPopup(callback, "switch to projectWindow",
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
        ChooseByNamePopup newPopup = new ChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex) {
            @Override
            protected void initUI(Callback callback, ModalityState modalityState, boolean allowMultipleSelection) {
                super.initUI(callback, modalityState, allowMultipleSelection);
                if (myTextField.getText().isEmpty()) {
                    myTextField.setText("*");
                }
                myTextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (myList.getSelectedValue() == EXTRA_ELEM) {
                            return;
                        }
//                        if (!e.isAltDown()) {
//                            return;
//                        }
                        int keyCode = e.getKeyCode();
                        switch (keyCode) {
                            case KeyEvent.VK_RIGHT:
                                ProjectNavigate elementAt = (ProjectNavigate) myList.getModel().getElementAt(getSelectedIndex());
                                ProjectManagerImpl.getInstance().closeProject(elementAt.getProject());
                                rebuildList(false);
                                break;
                        }
                    }
                });
            }
        };

        if (project != null) {
            project.putUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup);
        }
        return newPopup;
    }
}

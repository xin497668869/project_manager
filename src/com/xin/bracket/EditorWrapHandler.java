package com.xin.bracket;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public abstract class EditorWrapHandler extends EditorActionHandler {
    private EditorActionHandler orginEditorActionHandler;

    public EditorWrapHandler(String actionId) {
        super();
        EditorAction editorEnter = (EditorAction) ActionManager.getInstance().getAction(actionId);
        orginEditorActionHandler = editorEnter.getHandler();
        editorEnter.setupHandler(this);
    }

    public abstract boolean wrapIsEnabled(Editor editor, DataContext dataContext, Caret caret);

    public abstract void wrapExecute(Editor editor, DataContext dataContext, Caret caret);


    @Override
    protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
        return wrapIsEnabled(editor, dataContext, caret)
                || orginEditorActionHandler.isEnabled(editor, caret, dataContext);
    }


    @Override
    public boolean isEnabled(Editor editor, DataContext dataContext) {
        if (wrapIsEnabled(editor, dataContext, null)) {
            return true;
        }
        return orginEditorActionHandler.isEnabled(editor, dataContext);
    }

    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
        if (wrapIsEnabled(editor, dataContext, caret)) {
            wrapExecute(editor, dataContext, caret);
            return;
        }
        orginEditorActionHandler.execute(editor, caret, dataContext);
    }

    @Override
    public void execute(@NotNull Editor editor, @Nullable DataContext dataContext) {

        if (wrapIsEnabled(editor, dataContext, null)) {
            wrapExecute(editor, dataContext, null);
            return;
        }

        if (orginEditorActionHandler.isEnabled(editor, dataContext)) {
            orginEditorActionHandler.execute(editor, dataContext);
        }
    }
}

package com.xin.bracket;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
class StopEditorActionHandler extends EditorWrapHandler {

    public StopEditorActionHandler(String actionId) {
        super(actionId);
    }

    @Override
    public  boolean wrapIsEnabled(Editor editor, DataContext dataContext, Caret caret){
        return BracketPositionTemplate.isRunning();
    }


    @Override
    public  void wrapExecute(Editor editor, DataContext dataContext, Caret caret){
        BracketPositionTemplate.stop(editor);

        EditorAction editorStartNewLine = (EditorAction) ActionManager.getInstance().getAction("EditorStartNewLine");
        editorStartNewLine.getHandler().execute(editor,editor.getCaretModel().getCurrentCaret(),dataContext);
    }
}

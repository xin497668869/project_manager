package com.xin.bracket;

import com.intellij.openapi.editor.actionSystem.EditorAction;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class StopBracketPosition extends EditorAction {

    public StopBracketPosition() {
        super(new StopEditorActionHandler("EditorEnter"));
    }

}

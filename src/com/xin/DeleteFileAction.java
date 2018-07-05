package com.xin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.safeDelete.SafeDeleteHandler;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class DeleteFileAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor selectedTextEditor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        if(selectedTextEditor != null) {
            SafeDeleteHandler safeDeleteHandler = new SafeDeleteHandler();
            PsiFile psiFile = PsiDocumentManager.getInstance(e.getProject()).getPsiFile(selectedTextEditor.getDocument());
            safeDeleteHandler.invoke(e.getProject(), selectedTextEditor, psiFile, e.getDataContext());
        }
    }

}

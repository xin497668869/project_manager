/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xin.bracket;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import org.jetbrains.annotations.NotNull;

public class PreviousBracketPositionAction extends EditorAction {

    public PreviousBracketPositionAction() {
        super(new Handler( "EditorUnindentSelection"));
        setInjectedContext(true);
    }

    @Override
    public boolean startInTransaction() {
        return true;
    }

    private static class Handler extends EditorWrapHandler {

        public Handler(String actionId) {
            super(actionId);
        }

        @Override
        public void wrapExecute(Editor editor, DataContext dataContext, Caret caret) {
            if (!BracketPositionTemplate.positionTrail.isEmpty()) {
                Integer pop = BracketPositionTemplate.positionTrail.pop();
                BracketPositionTemplate.moveToOffset(editor,editor.getDocument().getCharsSequence(),pop,BracketPositionTemplate.rightBracket.getStartOffset(),false);
            }
        }

        @Override
        public boolean wrapIsEnabled(@NotNull Editor editor, DataContext dataContext, Caret caret) {
            return BracketPositionTemplate.isRunning();
        }

    }

}

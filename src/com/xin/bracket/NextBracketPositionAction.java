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

import java.util.Stack;

import static com.xin.bracket.BracketPositionTemplate.leftBracket;
import static com.xin.bracket.BracketPositionTemplate.moveToOffset;
import static com.xin.bracket.BracketPositionTemplate.rightBracket;
import static com.xin.bracket.BracketPositionTemplate.setSegmentHighlighter;

public class NextBracketPositionAction extends EditorAction {

    public NextBracketPositionAction() {
        super(new Handler("EditorTab"));
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

            CharSequence charsSequence = editor.getDocument().getCharsSequence();
            int offset = editor.getCaretModel().getCurrentCaret().getOffset();
            if (charsSequence.charAt(offset) != ')') {
                if (BracketPositionTemplate.isRunning()) {
                    int startOffset = rightBracket.getStartOffset();
                    editor.getCaretModel().getCurrentCaret().moveToOffset(startOffset);
                    return;
                }
                //如果括号在左边也识别一下
                if (charsSequence.charAt(offset - 1) == ')') {
                    offset--;
                } else {
                    return;
                }
            }
            if (leftBracket == null) {
                int startOffset = offset;
                while (startOffset >= 0) {
                    char c = charsSequence.charAt(startOffset);
                    if (c == '(') {
                        leftBracket = setSegmentHighlighter(editor, startOffset, startOffset + 1, leftBracket);
                        rightBracket = setSegmentHighlighter(editor, offset, offset + 1, rightBracket);
                        return ;
                    }
                    startOffset--;
                }
                if (startOffset < 0) {
                    return;
                }
            }

            int bracketPosition = offset;
            offset++;
            Stack<Character> bracketStack = new Stack<>();

            while (charsSequence.length() > offset) {
                char c = charsSequence.charAt(offset);
                if (c == '(') {
                    editor.getCaretModel().getCurrentCaret().moveToOffset(offset - 1);
                    bracketStack.push('(');
                }
                if (c == ')') {
                    if (bracketStack.isEmpty()) {
                        offset++;
                        moveToOffset(editor, charsSequence, offset, bracketPosition, true);
                        return;
                    }
                    Character pop = bracketStack.pop();
                    if (pop.equals('(')) {
                        offset++;
                        moveToOffset(editor, charsSequence, offset, bracketPosition, true);
                        return;
                    }
                }
                if (c == ',') {
                    if (bracketStack.isEmpty()) {
                        offset++;
                        moveToOffset(editor, charsSequence, offset-1, bracketPosition, true);
                        return;
                    }
                }
                if (c == ';') {
                    return;
                }
                offset++;
            }
        }


        @Override
        public boolean wrapIsEnabled(@NotNull Editor editor, DataContext dataContext, Caret caret) {
            CharSequence charsSequence = editor.getDocument().getCharsSequence();
            char c = charsSequence.charAt(editor.getCaretModel().getOffset());
            char r = charsSequence.charAt(editor.getCaretModel().getOffset()-1);
            return c == ')'|| r==')';
        }

    }

}

package com.xin.bracket;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class BracketPositionTemplate {
    public static RangeHighlighter leftBracket;
    public static RangeHighlighter rightBracket;

    public static Stack<Integer> positionTrail = new Stack<>();

    public static boolean isRunning() {
        return leftBracket != null || rightBracket != null;
    }

    public static void stop(Editor editor) {
        editor.getMarkupModel().removeAllHighlighters();
        leftBracket = null;
        rightBracket = null;
        BracketPositionTemplate.positionTrail.clear();
    }

    public static void moveToOffset(@NotNull Editor editor, CharSequence charsSequence, int offset, int bracketPosition, boolean stash) {
        /**
         * 看是否是向右跳还是向左跳, 如果是向右的话需要缓存路径, 左的话就只要pop出来就可以了
         */
        if (stash) {
            positionTrail.push(bracketPosition);
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            int noffset = offset;
            if (charsSequence.charAt(bracketPosition) == ')') {
                editor.getDocument().deleteString(bracketPosition, bracketPosition + 1);
                if (stash) {
                    noffset--;
                }
            }
            editor.getCaretModel().getCurrentCaret().moveToOffset(noffset);
            editor.getDocument().insertString(noffset, ")");
            rightBracket = setSegmentHighlighter(editor, noffset, noffset + 1, rightBracket);
        });
    }

    public static RangeHighlighter setSegmentHighlighter(Editor myEditor, int start, int end, RangeHighlighter rangeHighlighter) {

        if (rangeHighlighter != null) {
            myEditor.getMarkupModel().removeHighlighter(rangeHighlighter);
        }
        final TextAttributes lvAttr = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.LIVE_TEMPLATE_ATTRIBUTES);
        RangeHighlighter segmentHighlighter = myEditor.getMarkupModel()
                .addRangeHighlighter(start, end, HighlighterLayer.LAST + 1, lvAttr, HighlighterTargetArea.EXACT_RANGE);
        segmentHighlighter.setGreedyToLeft(true);
        segmentHighlighter.setGreedyToRight(true);
        rangeHighlighter = segmentHighlighter;
        return rangeHighlighter;
    }
}

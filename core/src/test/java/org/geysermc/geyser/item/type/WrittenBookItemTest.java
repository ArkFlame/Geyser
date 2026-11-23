package org.geysermc.geyser.item.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WrittenBookItemTest {
    @Test
    void boundedTextLeavesShortTextUntouched() {
        assertEquals("short", WrittenBookItem.boundedText("short", 256));
    }

    @Test
    void boundedTextTruncatesLongText() {
        String text = "a".repeat(300);
        assertEquals(256, WrittenBookItem.boundedPageText(text).length());
    }

    @Test
    void boundedTextDoesNotSplitSurrogatePair() {
        String text = "a".repeat(255) + "\uD83D\uDE00";
        assertEquals("a".repeat(255), WrittenBookItem.boundedPageText(text));
    }
}

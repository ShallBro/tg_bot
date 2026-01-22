package com.example.telegrambot.bot.view;

import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.callbacks.MenuAction;
import com.example.telegrambot.bot.callbacks.TagPagePayload;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.service.dto.TagSlice;
import com.example.telegrambot.service.dto.TagStat;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagListView {

    private final BotMessageService messages;
    private final CallbackCodec<TagPagePayload> tagNotesCallbackCodec;
    private final CallbackCodec<Integer> tagsPageCallbackCodec;
    private final CallbackCodec<MenuAction> menuCallbackCodec;

    public TagListView(BotMessageService messages, CallbackCodecRegistry registry) {
        this.messages = messages;
        this.tagNotesCallbackCodec = registry.get(CallbackType.TAG_NOTES);
        this.tagsPageCallbackCodec = registry.get(CallbackType.TAGS_PAGE);
        this.menuCallbackCodec = registry.get(CallbackType.MENU);
    }

    public String buildMessage(TagSlice slice) {
        StringBuilder sb = new StringBuilder(messages.text("tags.list.title"))
                .append("\n\n");

        List<TagStat> tags = slice.items();
        int offset = slice.page() * slice.size();

        for (int i = 0; i < tags.size(); i++) {
            TagStat stat = tags.get(i);
            sb.append(messages.text(
                    "tags.list.row",
                    offset + i + 1,
                    stat.name(),
                    stat.count()
            )).append("\n");
        }

        sb.append("\n")
                .append(messages.text("tags.list.footer"));

        return sb.toString();
    }

    public InlineKeyboardMarkup buildKeyboard(TagSlice slice) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>(2);

        for (TagStat tag : slice.items()) {
            row.add(InlineKeyboardButton.builder()
                    .text("#" + tag.name())
                    .callbackData(tagNotesCallbackCodec.encode(new TagPagePayload(tag.name(), 0)))
                    .build());

            if (row.size() == 2) {
                keyboard.add(row);
                row = new ArrayList<>(2);
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        List<InlineKeyboardButton> navRow = new ArrayList<>(2);
        if (slice.hasPrev()) {
            navRow.add(InlineKeyboardButton.builder()
                    .text(messages.text("tags.nav.prev"))
                    .callbackData(tagsPageCallbackCodec.encode(slice.page() - 1))
                    .build());
        }
        if (slice.hasNext()) {
            navRow.add(InlineKeyboardButton.builder()
                    .text(messages.text("tags.nav.next"))
                    .callbackData(tagsPageCallbackCodec.encode(slice.page() + 1))
                    .build());
        }
        if (!navRow.isEmpty()) {
            keyboard.add(navRow);
        }

        keyboard.add(List.of(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.back"))
                .callbackData(menuCallbackCodec.encode(MenuAction.BACK))
                .build()));

        return keyboard.isEmpty()
                ? null
                : InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }
}

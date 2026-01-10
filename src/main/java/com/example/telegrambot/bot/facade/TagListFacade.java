package com.example.telegrambot.bot.facade;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.bot.view.TagListView;
import com.example.telegrambot.service.TagService;
import com.example.telegrambot.service.dto.TagSlice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class TagListFacade {

    private static final int PAGE_SIZE = 10;

    private final TagService tagService;
    private final TelegramBotSender sender;
    private final BotMessageService messages;
    private final TagListView tagListView;

    public void sendPage(Long chatId, int page) {
        render(chatId, null, page, false);
    }

    public void editPage(Long chatId, Integer messageId, int page) {
        render(chatId, messageId, page, true);
    }

    private void render(Long chatId, Integer messageId, int page, boolean edit) {
        TagSlice slice = tagService.getTags(chatId, Math.max(page, 0), PAGE_SIZE);

        if (slice.items().isEmpty()) {
            if (page > 0) {
                render(chatId, messageId, 0, edit);
                return;
            }

            if (edit) {
                sender.editMarkdown(chatId, messageId, messages.text("tags.empty"), null);
            } else {
                sender.sendMarkdown(chatId, messages.text("tags.empty"), null);
            }
            return;
        }

        String text = tagListView.buildMessage(slice);
        InlineKeyboardMarkup keyboard = tagListView.buildKeyboard(slice);

        if (edit) {
            sender.editMarkdown(chatId, messageId, text, keyboard);
        } else {
            sender.sendMarkdown(chatId, text, keyboard);
        }
    }
}

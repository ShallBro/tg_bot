package com.example.telegrambot.bot.view;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.service.NoteService;
import com.example.telegrambot.service.dto.NoteSlice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class TagPagingFacade {

    private static final int PAGE_SIZE = 10;

    private final NoteService noteService;
    private final TelegramBotSender sender;
    private final TagPagingView tagPagingView;
    private final BotMessageService messages;

    public void sendFirstPage(Long chatId, String tag) {
        render(chatId, null, tag, 0, false);
    }

    public void render(Long chatId, Integer messageId, String tag, int page, boolean edit) {
        NoteSlice slice = noteService.findByTagPaged(chatId, tag, page, PAGE_SIZE);

        if (slice.items().isEmpty()) {
            if (page > 0) {
                render(chatId, messageId, tag, 0, edit);
                return;
            }

            if (edit) {
                sender.editMarkdown(chatId, messageId, messages.text("tag.command.empty", tag), null);
            } else {
                sender.sendMarkdown(chatId, messages.text("tag.command.empty", tag), null);
            }
            return;
        }

        String text = tagPagingView.buildMessage(tag, slice);
        InlineKeyboardMarkup inlineKeyboardMarkup = tagPagingView.buildKeyboard(tag, slice);

        if (edit) {
            sender.editMarkdown(chatId, messageId, text, inlineKeyboardMarkup);
        } else {
            sender.sendMarkdown(chatId, text, inlineKeyboardMarkup);
        }
    }
}

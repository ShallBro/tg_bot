package com.example.telegrambot.bot.facade;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.bot.view.TagPagingView;
import com.example.telegrambot.entity.Tag;
import com.example.telegrambot.service.NoteService;
import com.example.telegrambot.service.TagService;
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
    private final TagService tagService;
    private final TagPagingView tagPagingView;
    private final BotMessageService messages;

    public void sendFirstPage(Long chatId, String tagName) {
        Tag tag = tagService.requireByName(tagName);
        render(chatId, null, tag.getId(), tag.getName(), 0, false);
    }

    public void render(Long chatId, Integer messageId, Long tagId, String tagName, int page, boolean edit) {
        NoteSlice slice = noteService.findByTagIdPaged(chatId, tagId, page, PAGE_SIZE);

        if (slice.items().isEmpty()) {
            if (page > 0) {
                render(chatId, messageId, tagId, tagName, 0, edit);
                return;
            }

            if (edit) {
                sender.editMarkdown(chatId, messageId, messages.text("tag.command.empty", tagName), null);
            } else {
                sender.sendMarkdown(chatId, messages.text("tag.command.empty", tagName), null);
            }
            return;
        }

        String text = tagPagingView.buildMessage(tagName, slice);
        InlineKeyboardMarkup inlineKeyboardMarkup = tagPagingView.buildKeyboard(tagId, slice);

        if (edit) {
            sender.editMarkdown(chatId, messageId, text, inlineKeyboardMarkup);
        } else {
            sender.sendMarkdown(chatId, text, inlineKeyboardMarkup);
        }
    }
}

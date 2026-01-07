package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.view.TagPagingView;
import com.example.telegrambot.service.NoteService;
import com.example.telegrambot.utils.CommandPayloadExtractor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;
import java.util.regex.Pattern;

@Order(11)
@Component
@RequiredArgsConstructor
public class TagCommandHandler implements UpdateHandler {

    private static final int PAGE_SIZE = 10;

    private final NoteService noteService;
    private final TelegramBotSender sender;

    private static final Pattern TAG_CMD =
            Pattern.compile("^/tag(@\\w+)?(\\s|$)");

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && TAG_CMD.matcher(update.getMessage().getText().trim()).find();
    }

    @Override
    public void handle(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        Optional<String> payload = CommandPayloadExtractor.extract(msg.getText(), "/tag");
        if (payload.isEmpty()) {
            sender.sendMarkdown(chatId, "❌ Укажи тег.\nПример: `/tag backend`", null);
            return;
        }

        String tag = normalizeTag(payload.get());
        renderTagPage(chatId, null, tag, 0, false); // null messageId => send new message
    }

    private String normalizeTag(String raw) {
        String t = raw.trim();
        if (t.startsWith("#")) t = t.substring(1);
        return t.toLowerCase();
    }

    private void renderTagPage(Long chatId, Integer messageId, String tag, int page, boolean edit) {
        var slice = noteService.findByTagPaged(chatId, tag, page, PAGE_SIZE);

        if (slice.items().isEmpty() && page == 0) {
            sender.sendMarkdown(chatId, "❌ По тегу *#" + tag + "* заметок нет", null);
            return;
        }

        String text = TagPagingView.buildMessage(tag, slice);
        InlineKeyboardMarkup inlineKeyboardMarkup = TagPagingView.buildKeyboard(tag, slice);

        if (edit) {
            sender.editMarkdown(chatId, messageId, text, inlineKeyboardMarkup);
        } else {
            sender.sendMarkdown(chatId, text, inlineKeyboardMarkup);
        }
    }

}

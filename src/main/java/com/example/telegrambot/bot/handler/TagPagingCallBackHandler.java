package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.view.TagPagingView;
import com.example.telegrambot.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Order(10)
@Component
@RequiredArgsConstructor
public class TagPagingCallBackHandler implements UpdateHandler {

    private static final int PAGE_SIZE = 10;

    private final NoteService noteService;
    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().startsWith("tag|");
    }

    @Override
    public void handle(Update update) {
        var cq = update.getCallbackQuery();
        sender.answerCallback(cq.getId()); // чтобы “часики” на кнопке убрались

        Long chatId = cq.getMessage().getChatId();
        Integer messageId = cq.getMessage().getMessageId();

        String[] parts = cq.getData().split("\\|", 3);
        String tag = parts[1];
        int page = Integer.parseInt(parts[2]);

        var slice = noteService.findByTagPaged(chatId, tag, page, PAGE_SIZE);

        String text = TagPagingView.buildMessage(tag, slice);
        InlineKeyboardMarkup inlineKeyboardMarkup = TagPagingView.buildKeyboard(tag, slice);

        sender.editMarkdown(chatId, messageId, text, inlineKeyboardMarkup);
    }
}

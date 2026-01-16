package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.NoteCallbacks;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(9)
@RequiredArgsConstructor
public class NoteCallbackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final NoteCommandHandler noteCommandHandler;

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().startsWith("note|");
    }

    @Override
    public void handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        sender.answerCallback(callbackQuery.getId());

        Long chatId = resolveChatId(callbackQuery.getMessage());
        if (chatId == null) {
            return;
        }

        NoteCallbacks.parse(callbackQuery.getData())
                .ifPresent(noteId -> noteCommandHandler.sendNote(chatId, noteId));
    }

    private Long resolveChatId(MaybeInaccessibleMessage message) {
        if (message instanceof Message accessible) {
            return accessible.getChatId();
        }
        return null;
    }
}

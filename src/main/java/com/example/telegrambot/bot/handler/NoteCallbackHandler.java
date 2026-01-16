package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(9)
public class NoteCallbackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final NoteCommandHandler noteCommandHandler;
    private final CallbackCodec<Long> noteCallbackCodec;

    public NoteCallbackHandler(TelegramBotSender sender,
                               NoteCommandHandler noteCommandHandler,
                               CallbackCodecRegistry registry) {
        this.sender = sender;
        this.noteCommandHandler = noteCommandHandler;
        this.noteCallbackCodec = registry.get(CallbackType.NOTE);
    }

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

        noteCallbackCodec.decode(callbackQuery.getData())
                .ifPresent(noteId -> noteCommandHandler.sendNote(chatId, noteId));
    }

    private Long resolveChatId(MaybeInaccessibleMessage message) {
        if (message instanceof Message accessible) {
            return accessible.getChatId();
        }
        return null;
    }
}

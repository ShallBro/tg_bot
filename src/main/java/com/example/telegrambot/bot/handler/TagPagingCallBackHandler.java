package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.TagCallbacks;
import com.example.telegrambot.bot.facade.TagPagingFacade;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Order(10)
@Component
@RequiredArgsConstructor
public class TagPagingCallBackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final TagPagingFacade tagPagingFacade;

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().startsWith("tag|");
    }

    @Override
    public void handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        sender.answerCallback(callbackQuery.getId());

        TagCallbacks.parseTagNotes(callbackQuery.getData())
                .ifPresent(payload -> tagPagingFacade.render(
                        callbackQuery.getMessage().getChatId(),
                        callbackQuery.getMessage().getMessageId(),
                        payload.tag(),
                        payload.page(),
                        true
                ));
    }
}

package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.MenuCallbacks;
import com.example.telegrambot.bot.facade.TagListFacade;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@Order(7)
@RequiredArgsConstructor
public class MenuCallbackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final HelpCommandHandler helpCommandHandler;
    private final LastCommandHandler lastCommandHandler;
    private final TagListFacade tagListFacade;

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().startsWith("menu|");
    }

    @Override
    public void handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        sender.answerCallback(callbackQuery.getId());

        MenuCallbacks.parse(callbackQuery.getData())
                .ifPresent(action -> {
                    Optional<Update> wrapped = wrapMessage(callbackQuery.getMessage());
                    if (wrapped.isEmpty()) {
                        return;
                    }

                    Update wrappedUpdate = wrapped.get();
                    switch (action) {
                        case LAST -> lastCommandHandler.handle(wrappedUpdate);
                        case HELP -> helpCommandHandler.handle(wrappedUpdate);
                        case TAGS -> tagListFacade.sendPage(
                                wrappedUpdate.getMessage().getChatId(),
                                0
                        );
                    }
                });
    }

    private Optional<Update> wrapMessage(MaybeInaccessibleMessage message) {
        if (message instanceof Message accessible) {
            Update update = new Update();
            update.setMessage(accessible);
            return Optional.of(update);
        }
        return Optional.empty();
    }
}

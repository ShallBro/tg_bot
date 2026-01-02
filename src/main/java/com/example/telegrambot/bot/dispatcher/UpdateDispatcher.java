package com.example.telegrambot.bot.dispatcher;

import com.example.telegrambot.bot.handler.UpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateDispatcher {
    private final List<UpdateHandler> handlers;

    public void dispatch(Update update) {
        handlers.stream()
                .filter(handler -> handler.supports(update))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No handler found"))
                .handle(update);
    }
}

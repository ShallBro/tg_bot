package com.example.telegrambot.bot.message;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class BotMessageService {

    private static final Locale DEFAULT_LOCALE = new Locale("ru");

    private final MessageSource messageSource;

    public String text(String code, Object... args) {
        return messageSource.getMessage(code, args, DEFAULT_LOCALE);
    }
}

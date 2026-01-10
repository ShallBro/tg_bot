package com.example.telegrambot.bot.facade;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.view.MenuView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuFacade {

    private final TelegramBotSender sender;
    private final MenuView menuView;

    public void sendMenu(Long chatId) {
        sender.sendMarkdown(chatId, menuView.buildMessage(), menuView.buildKeyboard());
    }
}

package com.example.telegrambot.bot.view;

import com.example.telegrambot.bot.callbacks.MenuCallbacks;
import com.example.telegrambot.bot.message.BotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuView {

    private final BotMessageService messages;

    public String buildMessage() {
        return messages.text("menu.text");
    }

    public InlineKeyboardMarkup buildKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>(2);
        row.add(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.last"))
                .callbackData(MenuCallbacks.action(MenuCallbacks.MenuAction.LAST))
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.tags"))
                .callbackData(MenuCallbacks.action(MenuCallbacks.MenuAction.TAGS))
                .build());
        keyboard.add(row);

        keyboard.add(List.of(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.help"))
                .callbackData(MenuCallbacks.action(MenuCallbacks.MenuAction.HELP))
                .build()));

        return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }
}

package com.example.telegrambot.bot.view;

import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.callbacks.MenuCallbackCodec;
import com.example.telegrambot.bot.message.BotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuView {

    private final BotMessageService messages;
    private final CallbackCodec<MenuCallbackCodec.MenuAction> menuCallbackCodec;

    public MenuView(BotMessageService messages, CallbackCodecRegistry registry) {
        this.messages = messages;
        this.menuCallbackCodec = registry.get(CallbackType.MENU);
    }

    public String buildMessage() {
        return messages.text("menu.text");
    }

    public InlineKeyboardMarkup buildKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>(2);
        row.add(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.last"))
                .callbackData(menuCallbackCodec.encode(MenuCallbackCodec.MenuAction.LAST))
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.tags"))
                .callbackData(menuCallbackCodec.encode(MenuCallbackCodec.MenuAction.TAGS))
                .build());
        keyboard.add(row);

        keyboard.add(List.of(InlineKeyboardButton.builder()
                .text(messages.text("menu.button.help"))
                .callbackData(menuCallbackCodec.encode(MenuCallbackCodec.MenuAction.HELP))
                .build()));

        return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }
}

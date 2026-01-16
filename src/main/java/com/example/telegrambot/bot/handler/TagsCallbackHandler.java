package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.facade.TagListFacade;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(8)
public class TagsCallbackHandler implements UpdateHandler {

    private final TagListFacade tagListFacade;
    private final TelegramBotSender sender;
    private final CallbackCodec<Integer> tagsPageCallbackCodec;

    public TagsCallbackHandler(TagListFacade tagListFacade,
                               TelegramBotSender sender,
                               CallbackCodecRegistry registry) {
        this.tagListFacade = tagListFacade;
        this.sender = sender;
        this.tagsPageCallbackCodec = registry.get(CallbackType.TAGS_PAGE);
    }

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().startsWith("tags|");
    }

    @Override
    public void handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        sender.answerCallback(callbackQuery.getId());

        int page = tagsPageCallbackCodec.decode(callbackQuery.getData())
                .orElse(0);

        tagListFacade.editPage(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                page
        );
    }
}

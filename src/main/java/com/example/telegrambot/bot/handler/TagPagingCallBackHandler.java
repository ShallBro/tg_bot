package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.callbacks.TagPagePayload;
import com.example.telegrambot.bot.facade.TagPagingFacade;
import com.example.telegrambot.entity.Tag;
import com.example.telegrambot.service.TagService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Order(10)
@Component
public class TagPagingCallBackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final TagPagingFacade tagPagingFacade;
    private final TagService tagService;
    private final CallbackCodec<TagPagePayload> tagNotesCallbackCodec;

    public TagPagingCallBackHandler(TelegramBotSender sender,
                                    TagPagingFacade tagPagingFacade, TagService tagService,
                                    CallbackCodecRegistry registry) {
        this.sender = sender;
        this.tagPagingFacade = tagPagingFacade;
        this.tagService = tagService;
        this.tagNotesCallbackCodec = registry.get(CallbackType.TAG_NOTES);
    }

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
        tagNotesCallbackCodec.decode(callbackQuery.getData())
                .ifPresent(payload -> {
                            Long chatId = callbackQuery.getMessage().getChatId();
                            Integer messageId = callbackQuery.getMessage().getMessageId();
                            Tag tag = tagService.getTagById(payload.tagId());

                            if (tag == null) {
                                sender.editMarkdown(chatId, messageId, "❌ Тег не найден", null);
                                return;
                            }
                            tagPagingFacade.render(
                                    callbackQuery.getMessage().getChatId(),
                                    callbackQuery.getMessage().getMessageId(),
                                    tag.getId(),
                                    tag.getName(),
                                    payload.page(),
                                    true);
                        }
                );
    }
}

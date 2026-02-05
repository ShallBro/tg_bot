package com.example.telegrambot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class TelegramBotSender {
    private final DefaultAbsSender telegramSender;

    public void sendMarkdown(Long chatId, String text,  InlineKeyboardMarkup markup) {
        send(chatId, text, ParseMode.MARKDOWN, markup);
    }

    public void sendText(Long chatId, String text) {
        send(chatId, text, null, null);
    }

    public void sendText(Long chatId, String text, InlineKeyboardMarkup markup) {
        send(chatId, text, null, markup);
    }

    private void send(Long chatId, String text, String parseMode, InlineKeyboardMarkup markup) {
        try {
            SendMessage.SendMessageBuilder builder = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text);

            if (parseMode != null) {
                builder.parseMode(parseMode);
            }

            if (markup != null) {
                builder.replyMarkup(markup);
            }

            telegramSender.execute(builder.build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void editMarkdown(Long chatId, Integer messageId, String markdown, InlineKeyboardMarkup markup) {
        try {
            var editMessageTextBuilder = EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(markdown)
                    .parseMode(ParseMode.MARKDOWN);

            if (markup != null) editMessageTextBuilder.replyMarkup(markup);
            telegramSender.execute(editMessageTextBuilder.build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhoto(Long chatId, String fileId) {
        try {
            telegramSender.execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(fileId))
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDocument(Long chatId, String fileId) {
        try {
            telegramSender.execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(new InputFile(fileId))
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            telegramSender.execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void answerCallback(String callbackQueryId) {
        try {
            telegramSender.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

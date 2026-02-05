package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.bot.view.NoteView;
import com.example.telegrambot.service.EditStateService;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(8)
public class NoteEditCallbackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final NoteService noteService;
    private final EditStateService editStateService;
    private final NoteView noteView;
    private final BotMessageService messages;
    private final CallbackCodec<Long> noteEditCallbackCodec;
    private final CallbackCodec<Boolean> noteEditCancelCallbackCodec;

    public NoteEditCallbackHandler(TelegramBotSender sender,
                                   NoteService noteService,
                                   EditStateService editStateService,
                                   NoteView noteView,
                                   BotMessageService messages,
                                   CallbackCodecRegistry registry) {
        this.sender = sender;
        this.noteService = noteService;
        this.editStateService = editStateService;
        this.noteView = noteView;
        this.messages = messages;
        this.noteEditCallbackCodec = registry.get(CallbackType.NOTE_EDIT);
        this.noteEditCancelCallbackCodec = registry.get(CallbackType.NOTE_EDIT_CANCEL);
    }

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && (update.getCallbackQuery().getData().startsWith("note_edit|")
                || update.getCallbackQuery().getData().startsWith("note_edit_cancel|"));
    }

    @Override
    public void handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        sender.answerCallback(callbackQuery.getId());

        Long chatId = resolveChatId(callbackQuery.getMessage());
        if (chatId == null) {
            return;
        }

        String data = callbackQuery.getData();
        if (data.startsWith("note_edit|")) {
            noteEditCallbackCodec.decode(data).ifPresent(noteId ->
                    noteService.findNote(chatId, noteId).ifPresentOrElse(
                            note -> {
                                editStateService.start(chatId, noteId);
                                sender.sendText(chatId,
                                        messages.text("note.edit.prompt", noteId),
                                        noteView.buildEditKeyboard());
                            },
                            () -> sender.sendText(chatId, messages.text("note.edit.not_found", noteId))
                    )
            );
            return;
        }

        noteEditCancelCallbackCodec.decode(data).ifPresent(ignored -> {
            editStateService.cancel(chatId);
            sender.sendText(chatId, messages.text("note.edit.canceled"));
        });
    }

    private Long resolveChatId(MaybeInaccessibleMessage message) {
        if (message instanceof Message accessible) {
            return accessible.getChatId();
        }
        return null;
    }
}

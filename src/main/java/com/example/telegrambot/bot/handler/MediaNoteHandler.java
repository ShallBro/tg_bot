package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.entity.Note;
import com.example.telegrambot.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(5)
public class MediaNoteHandler implements UpdateHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && (update.getMessage().hasPhoto() || update.getMessage().hasDocument());
    }

    @Override
    public void handle(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();
        Long messageId = msg.getMessageId().longValue();

        List<NoteService.NoteAttachmentPayload> attachments = new ArrayList<>();
        if (msg.hasPhoto()) {
            PhotoSize photo = pickLargestPhoto(msg.getPhoto());
            if (photo != null) {
                attachments.add(new NoteService.NoteAttachmentPayload(
                        "PHOTO",
                        photo.getFileId(),
                        photo.getFileUniqueId(),
                        null,
                        null,
                        toLong(photo.getFileSize())
                ));
            }
        }

        if (msg.hasDocument()) {
            Document document = msg.getDocument();
            attachments.add(new NoteService.NoteAttachmentPayload(
                    "DOCUMENT",
                    document.getFileId(),
                    document.getFileUniqueId(),
                    document.getFileName(),
                    document.getMimeType(),
                    document.getFileSize()
            ));
        }

        if (attachments.isEmpty()) {
            return;
        }

        Note note = noteService.saveMediaNote(
                chatId,
                messageId,
                msg.getCaption(),
                msg.getMediaGroupId(),
                attachments
        );

        if (messageId.equals(note.getMessageId())) {
            sender.sendText(chatId, "✅ Заметка сохранена. id: %s".formatted(note.getId()));
        }
    }

    private PhotoSize pickLargestPhoto(List<PhotoSize> photos) {
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        return photos.getLast();
    }

    private Long toLong(Integer value) {
        return value == null ? null : value.longValue();
    }
}

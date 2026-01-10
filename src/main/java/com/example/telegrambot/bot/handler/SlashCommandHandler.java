package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.entity.NoteAttachment;
import com.example.telegrambot.service.NoteService;
import com.example.telegrambot.utils.CommandPayloadExtractor;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class SlashCommandHandler implements UpdateHandler {

    private final String command;
    private final Pattern pattern;
    private final NoteService noteService;
    private final TelegramBotSender sender;

    protected SlashCommandHandler(String command, NoteService noteService, TelegramBotSender sender) {
        this.command = command.startsWith("/") ? command : "/" + command;
        this.pattern = Pattern.compile("^" + Pattern.quote(this.command) + "(@\\w+)?(\\s|$)");
        this.noteService = noteService;
        this.sender = sender;
    }

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && pattern.matcher(update.getMessage().getText().trim()).find();
    }

    @Override
    public final void handle(Update update) {
        handleCommand(update);
    }

    protected Optional<String> extractPayload(Update update) {
        return CommandPayloadExtractor.extract(update.getMessage().getText(), command);
    }

    protected void sendAttachments(Long chatId, Long noteId) {
        List<NoteAttachment> attachments = noteService.getAttachments(noteId);
        for (NoteAttachment attachment : attachments) {
            if ("PHOTO".equalsIgnoreCase(attachment.getType())) {
                sender.sendPhoto(chatId, attachment.getFileId());
            } else if ("DOCUMENT".equalsIgnoreCase(attachment.getType())) {
                sender.sendDocument(chatId, attachment.getFileId());
            }
        }
    }

    protected String command() {
        return command;
    }

    protected abstract void handleCommand(Update update);
}

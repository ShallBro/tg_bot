package com.example.telegrambot.bot.handler;

import com.example.telegrambot.utils.CommandPayloadExtractor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.regex.Pattern;

public abstract class SlashCommandHandler implements UpdateHandler {

    private final String command;
    private final Pattern pattern;

    protected SlashCommandHandler(String command) {
        this.command = command.startsWith("/") ? command : "/" + command;
        this.pattern = Pattern.compile("^" + Pattern.quote(this.command) + "(@\\w+)?(\\s|$)");
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

    protected String command() {
        return command;
    }

    protected abstract void handleCommand(Update update);
}

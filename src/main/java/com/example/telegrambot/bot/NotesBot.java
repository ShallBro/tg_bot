package com.example.telegrambot.bot;

import com.example.telegrambot.bot.dispatcher.UpdateDispatcher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class NotesBot extends TelegramLongPollingBot {

    private final BotProperties props;
    private final UpdateDispatcher dispatcher;

    public NotesBot(BotProperties props, UpdateDispatcher dispatcher) {
        super(props.token());
        log.info("NotesBot CREATED. Username {}", props.username());
        this.props = props;
        this.dispatcher = dispatcher;
    }

    @PostConstruct
    public void deleteWebhook() {
        try {
            execute(new DeleteWebhook());
            log.info("Webhook deleted");
        } catch (Exception e) {
            log.warn("Failed to delete webhook: {}", e.getMessage());
        }
    }

    @PostConstruct
    public void testTelegram() {
        try {
            var me = execute(new GetMe());
            log.info("Telegram API OK. Bot id={}, username={}", me.getId(), me.getUserName());
        } catch (Exception e) {
            log.error("Telegram API FAIL: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return props.username();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Update received. hasMessage={}, text={}",
                update.hasMessage(),
                update.hasMessage() ? update.getMessage().getText() : null);
        dispatcher.dispatch(update);
    }
}

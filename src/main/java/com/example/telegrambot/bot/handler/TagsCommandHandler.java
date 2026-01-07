package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.TagService;
import com.example.telegrambot.service.dto.TagStat;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Pattern;

@Order(9)
@Component
@RequiredArgsConstructor
public class TagsCommandHandler implements UpdateHandler {

    private final TagService tagService;
    private final TelegramBotSender sender;

    private static final Pattern TAGS_CMD =
            Pattern.compile("^/tags(@\\w+)?(\\s|$)");

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && TAGS_CMD.matcher(update.getMessage().getText().trim()).find();
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        var tags = tagService.getTopTags(chatId, 30);

        if (tags.isEmpty()) {
            sender.sendMarkdown(chatId,
                    "Пока нет тегов. Добавь их в заметку: `#работа #идеи`", null);
            return;
        }

        StringBuilder sb = new StringBuilder("🏷️ *Теги*\n\n");
        for (int i = 0; i < tags.size(); i++) {
            TagStat t = tags.get(i);
            sb.append(i + 1)
                    .append(") #").append(t.name())
                    .append(" — ").append(t.count())
                    .append("\n");
        }

        sb.append("\nОткрыть: `/tag <имя>`");
        sender.sendMarkdown(chatId, sb.toString(), null);
    }
}

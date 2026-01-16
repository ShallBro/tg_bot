package com.example.telegrambot.bot.callbacks;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CallbackCodecRegistry {

    private final Map<CallbackType, CallbackCodec<?>> codecsMap = new EnumMap<>(CallbackType.class);
    private final List<CallbackCodec<?>> codecs;

    @PostConstruct
    public void registry() {
        for (CallbackCodec<?> codec : codecs) {
            codecsMap.put(codec.type(), codec);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> CallbackCodec<T> get(CallbackType type) {
        return (CallbackCodec<T>) codecsMap.get(type);
    }
}

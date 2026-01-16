package com.example.telegrambot.bot.callbacks;

import java.util.Optional;

public interface CallbackCodec<T> {
    CallbackType type();
    String encode(T value);
    Optional<T> decode(String data);
}

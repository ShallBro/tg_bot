package com.example.telegrambot.service.dto;

import java.util.List;

public record TagSlice(List<TagStat> items, boolean hasNext, boolean hasPrev, int page, int size) {
}

package com.example.telegrambot.utils;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TagParser {
    private static final Pattern TAG = Pattern.compile("(?<!\\w)#([a-zA-Z0-9_\\-]{1,32})");

    public Set<String> parse(String text) {
        Set<String> out = new LinkedHashSet<>();
        Matcher matcher = TAG.matcher(text == null ? "" : text);
        while (matcher.find()) out.add(matcher.group(1).toLowerCase());
        return out;
    }
}

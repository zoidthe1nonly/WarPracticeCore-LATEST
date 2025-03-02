package dev.zoid.warpracticecore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextStyle {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Map<String, Component> CACHE = new ConcurrentHashMap<>();

    public static Component parse(String text) {
        return CACHE.computeIfAbsent(text, mm::deserialize);
    }
}


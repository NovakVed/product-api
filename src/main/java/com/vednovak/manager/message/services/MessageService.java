package com.vednovak.manager.message.services;

public interface MessageService {
    String getMessage(String key);

    String getMessage(String key, Object... args);
}

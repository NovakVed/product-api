package com.vednovak.manager.message.services.impl;

import com.vednovak.manager.message.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class DefaultMessageService implements MessageService {

    private final MessageSource messageSource;

    public DefaultMessageService(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String key) {
        try {
            return messageSource.getMessage(key, null, Locale.getDefault());
        } catch (NoSuchMessageException ex) {
            log.error("Message key '{}' not found.", key);
            return "Message not found for key: " + key;
        }
    }
}

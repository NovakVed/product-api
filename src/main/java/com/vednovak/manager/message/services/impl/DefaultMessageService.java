package com.vednovak.manager.message.services.impl;

import com.vednovak.manager.message.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static com.vednovak.manager.message.utils.MessageConstants.MESSAGE_NOT_FOUND_FOR_KEY;

@Slf4j
@Service
public class DefaultMessageService implements MessageService {

    private final MessageSource messageSource;

    public DefaultMessageService(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // TODO: extend both methods below to accept locale as an argument and use this instead of Locale.getDefault()
    //  make sure when changing to Locale.getDefault that all log.err messages are taken from Locale.getDefault() or Locale.EN
    @Override
    public String getMessage(final String key) {
        try {
            return messageSource.getMessage(key, null, Locale.getDefault());
        } catch (final NoSuchMessageException ex) {
            log.error("Message key '{}' not found.", key, ex);
            return StringUtils.join(MESSAGE_NOT_FOUND_FOR_KEY, key);
        }
    }

    @Override
    public String getMessage(final String key, final Object... args) {
        try {
            return messageSource.getMessage(key, args, Locale.getDefault());
        } catch (final NoSuchMessageException ex) {
            log.error("Message key '{}' not found.", key, ex);
            return StringUtils.join(MESSAGE_NOT_FOUND_FOR_KEY, key);
        }
    }
}

package lk.tech.tgcontrollersocket.utils;

import lk.tech.tgcontrollersocket.dto.PrefixResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class BinaryUtils {

    public PrefixResult extractPrefix(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return new PrefixResult("", new byte[0]);
        }

        // ищем двоеточие (ограничим поиск первыми 64 байтами, чтобы не попасть в бинарные данные)
        int limit = Math.min(bytes.length, 64);
        for (int i = 0; i < limit; i++) {
            if (bytes[i] == ':') {
                String prefix = new String(bytes, 0, i, StandardCharsets.UTF_8);
                byte[] data = Arrays.copyOfRange(bytes, i + 1, bytes.length);
                return new PrefixResult(prefix, data);
            }
        }

        // если двоеточие не найдено — возвращаем пустой префикс и все данные как есть
        return new PrefixResult("", bytes);
    }
}
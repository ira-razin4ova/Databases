package ru.hogwarts.school.service;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.BadRequestException;

import java.util.Map;

@Service
public class DataCodecService {
    private static final int SECRET1 = 5;
    private static final int SECRET2 = 8;

    private final Map<Integer, Character> mapCharsFist = Map.of(
            1, '"',
            4, '#',
            5, '%',
            8, '&',
            11, '~',
            15, '$',
            18, '*'

    );

    public String encodePhone(String rawPhone) {
        String normalized = normalizePhoneNumber(rawPhone);
        return encode(normalized);
    }

    public String normalizePhoneNumber(String phoneNumber) {
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        if (cleaned.length() != 11) {
            throw new BadRequestException("Номер телефона не корректен");
        }
        if (cleaned.startsWith("8")) {
            cleaned = "7" + cleaned.substring(1);
        }
        if (!cleaned.startsWith("79")) {
            throw new BadRequestException("Номер должен начинаться с 79");
        }
        return cleaned;
    }

    public String encode(String number) {
        StringBuilder result = new StringBuilder();

        int first = number.charAt(0) - '0';
        int last = number.charAt(number.length() - 1) - '0';
        int sum = first + last;
        Character flagSymbol = mapCharsFist.get(sum);
        boolean reverse = false;

        if (flagSymbol != null) {
            reverse = true;
        }

        for (int i = 0; i < number.length(); i++) {
            int value = number.charAt(i) - '0' + SECRET1 + SECRET2 + 45;
            char ch = (char) value;
            result.append(ch);
        }

        if (reverse) {
            result.append(flagSymbol).reverse();
        }

        return result.toString();
    }

    @Named("decodePhone")
    public String decode(String chars) {
        if (chars == null) return null;
        String data = extractAndHandleReverse(chars);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            int value = data.charAt(i) - SECRET1 - SECRET2 - 45;
            result.append(value);
        }
        return result.toString();
    }

    private String extractAndHandleReverse(String value) {

        char firstSymbol = value.charAt(0);

        boolean reverse = mapCharsFist.containsValue(firstSymbol);

        String data = reverse ? value.substring(1) : value;

        return reverse
                ? new StringBuilder(data).reverse().toString()
                : data;
    }

}


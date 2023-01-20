package com.comet.nfc_sever.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StringUtil {

    public static boolean isUUID(String input) {
        try {
            UUID.fromString(input);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String generateRandomString(int size) {
        int left = 48;
        int right = 122;
        ThreadLocalRandom random = ThreadLocalRandom.current(); //효과 좋은 랜덤
        return random.ints(left, right + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) //첫번째 인자 -> 생성할 객체, 두번째 인자 -> 들어온 값 어떻게 처리할지, 세번째 인자 -> 병렬처리에서 나온 결과 어떻게 할지
                .toString();
    }
}

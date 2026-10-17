package br.com.marceloneuro.mtgtrade.shared.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class HashUtil {

    public static String gerarCheckSum(String payload) {
        if (payload == null) {
            return null;
        }

        return DigestUtils.md5DigestAsHex(payload.getBytes(StandardCharsets.UTF_8));
    }
}

package com.pepej.papi.utils;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Character.toUpperCase;

public final class StringUtils {

    private static final String NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ThreadLocal<Map<String, DecimalFormat>> formattersMap = ThreadLocal.withInitial(HashMap::new);

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    public static boolean isNonEmpty(String s) {
        return !isEmpty(s);
    }

    public static String plural(int count, String single, String pluralA, String pluralB) {

        int h = count % 100;
        int t = count % 10;
        if (h > 10 && h < 20) {
            return pluralB;
        }
        if (t > 1 && t < 5) {
            return pluralA;
        }
        if (t == 1) {
            return single;
        }
        return pluralB;
    }
    public static String getRandomString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

    public static String getCountMessage(final String key, final long count) {
        return key + "_" + getDeclination(count);
    }

    public static int getDeclination(final long count) {
        long n = Math.abs(count) % 100;
        long n1 = n % 10;

        if (n > 10 && n < 20) {
            return 0;
        } else if (n1 > 1 && n1 < 5) {
            return 2;
        } else if (n1 == 1) {
            return 1;
        }

        return 0;
    }

    public static String getProgressBar(double current, double max, int totalBars, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        double percent = current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars) + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

    private static DecimalFormat getFormatter(final String format) {
        return formattersMap.get().computeIfAbsent(format, StringUtils::initFormat);
    }

    public static String formatAmount(BigDecimal amount, String format) {
        return format(amount, getFormatter(format));
    }

    public static String formatAmount(double amount, String format) {
        return format(amount, getFormatter(format));
    }

    public static String trim(String s) {
        if (isEmpty(s)) return s;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (!Character.isWhitespace(c) && c != 160) { // 160 - is whitespace used in Excel files
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static boolean parseBoolean(String s) {
        return s != null && (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("да"));
    }

    public static String capitalizeFirstLetter(String s) {
        if (isEmpty(s)) return s;
        return toUpperCase(s.trim().charAt(0)) + s.trim().substring(1);
    }

    public static boolean equalsIgnoreCase(String s1, String s2) {
        if (isEmpty(s1) ^ isEmpty(s2)) return false;
        if (isEmpty(s1) && isEmpty(s2)) return true;
        return s1.equalsIgnoreCase(s2);
    }

    public static String getRandomCode(int length) {
        return getRandomString(length, NUMBERS);
    }

    public static String getRandomString(int length, String alphabet) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int charAt = RANDOM.nextInt(alphabet.length());
            sb.append(alphabet.charAt(charAt));
        }
        return sb.toString();
    }

    private static String format(Object o, Format format) {
        if (o == null) {
            return "";
        }

        return format.format(o);
    }

    private static DecimalFormat initFormat(String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);

        DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(' ');

        decimalFormat.setDecimalFormatSymbols(dfs);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

        return decimalFormat;
    }

    private StringUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
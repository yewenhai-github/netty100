package com.netty100.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.ObjectUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @author why
 */
@UtilityClass
public class MathUtil {

    public BigDecimal reserveTwoDigits(long a, long b) {
        if (Objects.equals(b, 0L)) {
            return new BigDecimal(0);
        }
        double res = (double) a * 100 / b;
        DecimalFormat df = new DecimalFormat("###.00");
        return new BigDecimal(df.format(res));
    }

    public boolean compare(BigDecimal source, BigDecimal dest) {
        return source.compareTo(dest) < 1;
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof String || value instanceof Long || value instanceof Integer || value instanceof Double) {
            return new BigDecimal(ObjectUtils.toString(value));
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal getPrettyNumber(BigDecimal dec) {
        return new BigDecimal(dec.stripTrailingZeros().toPlainString());
    }
}

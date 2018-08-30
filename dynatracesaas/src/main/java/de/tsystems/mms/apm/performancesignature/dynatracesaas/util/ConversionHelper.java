/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatracesaas.util;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.UnitEnum;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.UnitEnum.*;

public class ConversionHelper {
    public static final List<UnitEnum> TIME_UNITS = Arrays.asList(
            UnitEnum.NANOSECOND,
            UnitEnum.MICROSECOND);
    private static final int UNIT = 1000;
    private static List<UnitEnum> UNITS = Arrays.asList(
            BYTE,
            KILOBYTE,
            MEGABYTE,
            GIGABYTE
    );
    private static List<UnitEnum> UNITS_PER_SECOND = Arrays.asList(
            BYTEPERSECOND,
            KILOBYTEPERSECOND,
            MEGABYTEPERSECOND
    );

    private ConversionHelper() {
    }

    public static Double convertUnit(Double value, UnitEnum from, UnitEnum to) {
        if (value == null || from == null || to == null) return value;
        if (from == to) return value;

        if (UNITS.contains(from) && UNITS.contains(to)) {
            int exp = UNITS.indexOf(to) - UNITS.indexOf(from);
            value = value / (Math.pow(UNIT, exp));
        } else if (UNITS_PER_SECOND.contains(from) && UNITS_PER_SECOND.contains(to)) {
            int exp = UNITS_PER_SECOND.indexOf(to) - UNITS_PER_SECOND.indexOf(from);
            value = value / (Math.pow(UNIT, exp));
        } else if (from == UnitEnum.NANOSECOND || from == UnitEnum.MICROSECOND) {
            return convertTimeUnit(value, from);
        }
        return PerfSigUIUtils.roundAsDouble(value);
    }

    private static Double convertTimeUnit(Double value, UnitEnum from) {
        if (from == UnitEnum.NANOSECOND) {
            value = (double) TimeUnit.NANOSECONDS.toMillis(value.longValue());
        } else if (from == UnitEnum.MICROSECOND) {
            value = (double) TimeUnit.MICROSECONDS.toMillis(value.longValue());
        }
        return PerfSigUIUtils.roundAsDouble(value);
    }

    static Double convertByteUnit(double value, UnitEnum from) {
        value = convertToByte(value, from);
        int exp = (int) (Math.log(value) / Math.log(UNIT));
        if (UNITS.contains(from)) {
            if (exp > 3) exp = 3;
            return value / (Math.pow(UNIT, exp));
        } else if (UNITS_PER_SECOND.contains(from)) {
            if (exp > 2) exp = 2;
            return value / (Math.pow(UNIT, exp));
        } else {
            return value;
        }
    }

    public static UnitEnum convertByteUnitEnum(double value, UnitEnum from) {
        value = convertToByte(value, from);

        if (value < UNIT) return from;
        int exp = (int) (Math.log(value) / Math.log(UNIT));
        if (UNITS.contains(from)) {
            if (exp > 3) return UNITS.get(UNITS.size() - 1);
            return UNITS.get(exp);
        } else if (UNITS_PER_SECOND.contains(from)) {
            if (exp > 2) return UNITS_PER_SECOND.get(UNITS_PER_SECOND.size() - 1);
            return UNITS_PER_SECOND.get(exp);
        } else {
            return from;
        }
    }

    private static double convertToByte(final double value, UnitEnum from) {
        switch (from) {
            case KILOBYTE:
            case KILOBYTEPERSECOND:
            case KILOBYTEPERMINUTE:
                return value * 1000;
            case MEGABYTE:
            case MEBIBYTEPERMINUTE:
                return value * 1000000;
            case GIGABYTE:
            case MEGABYTEPERSECOND:
                return value;
        }
        return value;
    }
}

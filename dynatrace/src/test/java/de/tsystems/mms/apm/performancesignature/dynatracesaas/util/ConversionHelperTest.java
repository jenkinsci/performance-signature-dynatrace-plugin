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
import org.apache.commons.lang.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;

public class ConversionHelperTest {

    @Test
    public void convertUnit() {
        double value = 110592D;
        UnitEnum from = UnitEnum.BYTE;
        UnitEnum to = ConversionHelper.convertByteUnitEnum(value, from);
        Assert.assertEquals(UnitEnum.MEGABYTE, to);
        Assert.assertEquals(110.592, ConversionHelper.convertByteUnit(value, from), 0.001);
    }

    @Test
    public void convertUnit2() {
        double value = 1855425871872D;
        UnitEnum from = UnitEnum.BYTE;
        UnitEnum to = ConversionHelper.convertByteUnitEnum(value, from);
        Assert.assertEquals(UnitEnum.MEGABYTE, to);
        Assert.assertEquals(1855.425871872, ConversionHelper.convertByteUnit(value, from), 0.001);
    }

    @Test
    public void convertUnit3() {
        double value = 110592D;
        UnitEnum from = UnitEnum.KILOBYTEPERSECOND;
        UnitEnum to = ConversionHelper.convertByteUnitEnum(value, from);
        Assert.assertEquals(UnitEnum.MEGABYTEPERSECOND, to);
        Assert.assertEquals(110.592, ConversionHelper.convertByteUnit(value, from), 0.001);
    }

    @Test
    public void convertUnit4() {
        double value = 1855425872D;
        UnitEnum from = UnitEnum.KILOBYTEPERSECOND;
        UnitEnum to = ConversionHelper.convertByteUnitEnum(value, from);
        Assert.assertEquals(UnitEnum.MEGABYTEPERSECOND, to);
        Assert.assertEquals(1855425.872, ConversionHelper.convertByteUnit(value, from), 0.001);
    }

    @Test
    public void convertByteUnitEnum() {
        Assert.assertEquals(UnitEnum.BYTE, ConversionHelper.convertByteUnitEnum(0D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.BYTE, ConversionHelper.convertByteUnitEnum(27D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.BYTE, ConversionHelper.convertByteUnitEnum(999D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(1000D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(1023D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(1024D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(1728D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(110592D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(7077888D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(452984832D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(28991029248D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(1855425871872D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(9223372036854775807D, UnitEnum.BYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(9000D, UnitEnum.KILOBYTE));
        Assert.assertEquals(UnitEnum.MEGABYTE, ConversionHelper.convertByteUnitEnum(9000D, UnitEnum.MEGABYTE));
    }

    @Test
    public void compareNumbers() {
        Double lowerWarning = 90D;
        Double lowerSevere = 80D;
        Double upperWarning = 270D;
        Double upperSevere = 290D;
        Double value = 291D;
        if (ObjectUtils.compare(value, lowerWarning) <= 0 && ObjectUtils.compare(value, lowerSevere) > 0) {
            System.out.println("lowerWarning");
        } else if (ObjectUtils.compare(value, lowerSevere) <= 0) {
            System.out.println("lowerSevere");
        } else if (ObjectUtils.compare(value, upperWarning) >= 0 && ObjectUtils.compare(value, upperSevere) < 0) {
            System.out.println("upperWarning");
        } else if (ObjectUtils.compare(value, upperSevere) >= 0) {
            System.out.println("upperSevere");
        }
    }
}
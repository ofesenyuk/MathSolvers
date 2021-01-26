/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import com.helger.commons.math.MathHelper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utilities to compare expected results with found solutions 
 * @author OFeseniuk
 */
public class ComparatorTestUtils {
    
    public static void compareArraysWithDoublePrecision(List<Number> expResult,
            Function<List<Number>, List<Number>> getResult,
            final double precision) {
        Object[] result = toSortedDoubleArray(getResult.apply(expResult));
        Object[] expResultSorted = toSortedDoubleArray(expResult);
        final String expStr = Arrays.toString(expResultSorted);
        final String resStr = Arrays.toString(result);

        assertEquals(expResultSorted.length, result.length, () -> String
                .format("Arrays %s %s should be of the same size ",
                        expStr, resStr));

        for (int i = 0; i < expResultSorted.length; i++) {
            assertEquals((double) expResultSorted[i], (double) result[i],
                precision * 10,
                () -> String
                    .format("Root is not found with given precision  %s %s",
                            expStr, resStr));
        }
    }
    
    public static void compareArraysWithBigDecimalPrecision(
            List<Number> expResult,
            Function<List<Number>, List<Number>> getResult,
            final BigDecimal precision) {
        List<BigDecimal> result 
                = toSortedBigDecimalArray(getResult.apply(expResult));
        List<BigDecimal> expResultSorted = toSortedBigDecimalArray(expResult);

        assertEquals(expResultSorted.size(), result.size(),
                "Arrays should be of the same size");

        for (int i = 0; i < expResultSorted.size(); i++) {
            assertTrue(expResultSorted.get(i).subtract(result.get(i)).abs()
                            .compareTo(precision.scaleByPowerOfTen(10)) <= 0,
                    "Root is not found with given precision");
        }
    }

    private static Object[] toSortedDoubleArray(List<Number> expResult) {
        return expResult.stream()
                .map(Number::doubleValue)
                .sorted()
                .toArray();
    }

    private static List<BigDecimal> toSortedBigDecimalArray(List<Number> list) {
        return list.stream()
                .map(MathHelper::toBigDecimal)
                .sorted()
                .collect(Collectors.toList());
    }
}

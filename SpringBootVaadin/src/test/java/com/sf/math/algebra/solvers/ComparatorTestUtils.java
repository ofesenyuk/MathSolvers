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
import org.junit.Assert;

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

        Assert.assertEquals(String
                .format("Arrays %s %s should be of the same size ", 
                        expStr, resStr), 
            expResultSorted.length, result.length);

        for (int i = 0; i < expResultSorted.length; i++) {
            Assert.assertEquals(String
                    .format("Root is not found with given precision  %s %s",
                            expStr, resStr) ,
                    (double) expResultSorted[i], (double) result[i],
                    precision * 10);
        }
    }
    
    public static void compareArraysWithBigDecimalPrecision(
            List<Number> expResult,
            Function<List<Number>, List<Number>> getResult,
            final BigDecimal precision) {
        List<BigDecimal> result 
                = toSortedBigDecimalArray(getResult.apply(expResult));
        List<BigDecimal> expResultSorted = toSortedBigDecimalArray(expResult);

        Assert.assertEquals("Arrays should be of the same size", 
                expResultSorted.size(), result.size());

        for (int i = 0; i < expResultSorted.size(); i++) {
            Assert.assertTrue("Root is not found with given precision",
                    expResultSorted.get(i).subtract(result.get(i)).abs()
                            .compareTo(precision.scaleByPowerOfTen(10)) <= 0);
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

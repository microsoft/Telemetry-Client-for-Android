package com.microsoft.cll;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.Exception;

public class CorrelationVectorTests {

    @Test
    public void testSetCorrectValue() {
        CorrelationVector cV = new CorrelationVector();
        cV.SetValue("AAAAAAAAAAAAAAAA.1");
    }

    @Test
    public void testSetIncorrectLongValue() {
        try {
            // 17 characters instead of 16 which should throw an exception
            CorrelationVector cV = new CorrelationVector();
            cV.SetValue("AAAAAAAAAAAAAAAAA.1");
            fail("Invalid correlation value was accepted - Too Long");
        } catch (Exception e) {
        }
    }

    @Test
    public void testSetIncorrectNegativeValue() {
        try {
            CorrelationVector cV = new CorrelationVector();
            cV.SetValue("AAAAAAAAAAAAAAAA.-1");
            fail("Invalid correlation value was accepted - Negative Number");
        }catch (Exception e) {
        }
    }

    @Test
    public void testOverflowValue() {
        CorrelationVector cV = new CorrelationVector();
        cV.SetValue("AAAAAAAAAAAAAAAA." + Integer.MAX_VALUE);
        String val = cV.GetValue();
        cV.Increment();
        assert(val.equals(cV.GetValue()));
    }

    @Test
    public void testExpandValue() {
        CorrelationVector cV = new CorrelationVector();
        cV.SetValue("AAAAAAAAAAAAAAAA.1");
        String val = cV.GetValue() + ".1";
        cV.Extend();
        assert(val.equals(cV.GetValue()));
    }

    @Test
    public void testCannotExpandValue() {
        CorrelationVector cV = new CorrelationVector();
        cV.SetValue("AAAAAAAAAAAAAAAA.12345.12345.12345.12345.12345.12345.12345.1234");
        String val = cV.GetValue();
        cV.Extend();
        assert(val.equals(cV.GetValue()));
    }

    @Test
    public void testCannotIncrementValue() {
        CorrelationVector cV = new CorrelationVector();
        cV.SetValue("AAAAAAAAAAAAAAAA.12345.12345.12345.12345.12345.12345.12345.9999");
        String val = cV.GetValue();
        cV.Increment();
        assert(val.equals(cV.GetValue()));
    }
}
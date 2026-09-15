package com.orm.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CheckValueIn
{
public String[] stringValues() default{};
public long[] longValues() default;
public int[] intValues() default{};
public short[] shortValues() default{};
public byte[] byteValues() default{};
public double[] doubleValues() default{};
public float[] floatValues() default{};
public char[] charValues() default{};
}
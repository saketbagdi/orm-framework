package com.orm.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CompositeForeignKey
{
public String parentTableName();
public String[] columns();
}
package com.orm.db;
import java.util.*;
public class ForeignKey
{
private String  parentTableName;
private List<ForeignKeyColumnMapping> columnMappings;
public ForeignKey()
{
this.parentTableName="";
this.columnMappings=null;
}
public void setParentTableName(java.lang.String parentTableName)
{
this.parentTableName=parentTableName;
}
public java.lang.String getParentTableName()
{
return this.parentTableName;
}
public void setColumnMappings(java.util.List columnMappings)
{
this.columnMappings=columnMappings;
}
public java.util.List getColumnMappings()
{
return this.columnMappings;
}
}

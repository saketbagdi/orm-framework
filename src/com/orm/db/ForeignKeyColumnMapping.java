package com.orm.db;
public class ForeignKeyColumnMapping
{
private String childTableColumnName;
private String parentTableColumnName;
public ForeignKeyColumnMapping()
{
this.childTableColumnName="";
this.parentTableColumnName="";
}
public void setChildTableColumnName(java.lang.String childTableColumnName)
{
this.childTableColumnName=childTableColumnName;
}
public java.lang.String getChildTableColumnName()
{
return this.childTableColumnName;
}
public void setParentTableColumnName(java.lang.String parentTableColumnName)
{
this.parentTableColumnName=parentTableColumnName;
}
public java.lang.String getParentTableColumnName()
{
return this.parentTableColumnName;
}
}

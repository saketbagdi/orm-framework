package com.orm.db;
public class TableColumn
{
private String name;
private String Type;
private int width;
private int precision;
private boolean isPrimaryKey;
private boolean isNullable;
private boolean isUnique;
private String defaultValue;
public TableColumn()
{
this.name="";
this.type="";
this.widht=0;
this.precision=0;
this.PrimaryKey=false;
this.isAutoIncrement=false;
this.isNullable=false;
this.isUnique=false;
this.defaultValue="";
}
public void setname(java.lang.String name)
{
this.name=name;
}
public java.lang.String getName()
{
return this.name;
}
public void setType(java.lang.String type)
{
this.type=type;
}
public void setWidth(int width)
{
this.width=width;
}
public int getWidth()
{
return this.width;
}
public void setPrecision(int precision)
{
this.precision=precision;
}
public int getPrecision()
{
return this.precision;
}
public void setisPrimaryKey(boolean isPrimaryKey)
{
this.isPrimaryKey=isPrimaryKey;
}
public boolean getIsPrimaryKey()
{
return this.isPrimaryKey;
}
public void setIsAutoIncrement(boolean isAutoIncrement)
{
this.isAutoIncrement=isAutoIncrement;
}
public boolean getIsAutoIncrement()
{
return this.isAutoIncrement;
}
public void setIsNullable(boolean isNullable)
{
this.isNullable=isNullable;
}
public boolean getIsNullable()
{
return this.isNullable;
}
public void setIsUnique(boolean isUnique)
{
this.isUnique=isUnique;
}
public boolean getIsUnique()
{
return this.isUnique;
}
public void setDefaultValue(java.lang.String defaultValue)
{
this.defaultValue=defaultValue;
}
public java.lang.String getDefaultValue()
{
return this.defaultValue;
}
}
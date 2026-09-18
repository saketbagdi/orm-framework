package com.orm.db;
import java.util.*;
public class Table
{
private String name;
private List<TableColumn> tableColumns;
private List<ForeignKey> foreignKeys;
public Table()
{
this.name="";
this.tableColumns=null;
this.foreignKeys=null;
}
public void setName(java.lang.String name)
{
this.name=name;
}
public java.lang.String getName()
{
return this.name;
}
public void setTableColumns(java.util.List tableColumns)
{
this.tableColumns=tableColumns;
}
public java.util.List getTableColumns()
{
return this.tableColumns;
}
public void setForeignKeys(java.util.List foreignKeys)
{
this.foreignKeys=foreignKeys;
}
public java.util.List getForeignKeys()
{
return this.foreignKeys;
}
}
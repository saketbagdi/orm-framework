package com.orm.db;
import java.util.*;
public class Database
{
private List<Table> tables;
public void setTables(List<Table> tables)
{
this.tables=tables;
}
public List<Table> getTables()
{
return this.tables;
}
}

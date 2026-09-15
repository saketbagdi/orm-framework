package com.orm.db;
import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
import com.orm.exceptions.*;
public class DatabaseFactory
{
private DatabaseFactory(){};
public static Database getDatabase(Connection connection) throws DatabaseException
{
Database database=new Database();
try
{
DatabaseMetaData databaseMetaData;
databaseMetaData=connection.getMetaData();
String catalog;
catalog=connection.getCatalog();
List<Table> tablesList;
List<TableColumn> tableColumns;
ResultSet tablesResultSet=databaseMetaData.getTables(catalog,null,"%",new String[]{"TABLE"});
String tableName;
Table table;
Set<String> primaryKeysSet;
ResultSet primaryKeysResultSet;
String primaryKeyColumnName;
Set<String> uniqueKeysSet;
ResultSet uniqueKeysResultSet;
String uniqueKeyColumnName;
tablesList=new ArrayList<>();
while(tablesResultSet.next())
{
tableName=tablesResultSet.getString("TABLE_NAME");
if(tableName!=null) tableName=tableName.trim();
if(tableName==null) continue;
primaryKeysSet=new HashSet<>();
primaryKeysResultSet=databaseMetaData.getPrimaryKeys(catalog,null,tableName);
while(primaryKeysResultSet.next())
{
primaryKeyColumnName=primaryKeysResultSet.getString("COLUMN_NAME");
if(primaryKeyColumnName!=null) primaryKeyColumnName=primaryKeyColumnName.trim();
primaryKeysSet.add(primaryKeyColumnName);
}
primaryKeysResultSet.close();
uniqueKeysSet=new HashSet<>();
uniqueKeysResultSet=databaseMetaData.getIndexInfo(catalog,null,tableName,true,false);
while(uniqueKeysResultSet.next())
{
uniqueKeyColumnName=uniqueKeysResultSet.getString("COLUMN_NAME");
if(uniqueKeyColumnName!=null) uniqueKeyColumnName=uniqueKeyColumnName.trim();
uniqueKeysSet.add(uniqueKeyColumnName);
}
uniqueKeysResultSet.close();
table=new Table();
setField(table,"name",tableName);
tableColumns=new ArrayList<>();
ResultSet columnResultSet=databaseMetaData.getColumns(catalog,null,tableName,"%");
while(columnResultSet.next())
{
String columnName=columnResultSet.getString("COLUMN_NAME");
if(columnName!=null) columnName=columnName.trim();
TableColumn tableColumn=new TableColumn();
setField(tableColumn,"name",columnName);
setField(tableColumn,"type",columnResultSet.getString("TYPE_NAME"));
setField(tableColumn,"width",columnResultSet.getInt("COLUMN_SIZE"));
setField(tableColumn,"precision",columnResultSet.getInt("DECIMAL_DIGITS"));
setField(tableColumn,"isPrimaryKey",primaryKeysSet.contains(columnName));
setField(tableColumn,"isUnique",uniqueKeysSet.contains(columnName));
setField(tableColumn,"isNullable",columnResultSet.getInt("NULLABLE")==DatabaseMetaData.columnNullable);
setField(tableColumn,"defaultValue",columnResultSet.getString("COLUMN_DEF"));
setField(tableColumn,"isAutoIncrement","YES".equalsIgnoreCase(columnResultSet.getString("IS_AUTOINCREMENT")));
tableColumns.add(tableColumn);
}
columnResultSet.close();
setField(table,"tableColumns",tableColumns);
tablesList.add(table);
}
tablesResultSet.close();
setField(database,"tables",tablesList);
}catch(SQLException sqlException)
{
throw new DatabaseException("Unable to read database metadata: "+sqlException.getMessage());
}
return database;
}
private static void setField(Object object,String fieldName,Object value) throws DatabaseException
{
try
{
Field field=object.getClass().getDeclaredField(fieldName);
field.setAccessible(true);
field.set(object,value);
}catch(Exception exception)
{
throw new DatabaseException("Unable to set field "+fieldName+": "+exception.getMessage());
}
}
}package com.orm.db;
import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
import com.orm.exceptions.*;
public class DatabaseFactory
{
private DatabaseFactory(){};
public static Database getDatabase(Connection connection) throws DatabaseException
{
Database database=new Database();
try
{
DatabaseMetaData databaseMetaData;
databaseMetaData=connection.getMetaData();
String catalog;
catalog=connection.getCatalog();
List<Table> tablesList;
List<TableColumn> tableColumns;
ResultSet tablesResultSet=databaseMetaData.getTables(catalog,null,"%",new String[]{"TABLE"});
String tableName;
Table table;
Set<String> primaryKeysSet;
ResultSet primaryKeysResultSet;
String primaryKeyColumnName;
Set<String> uniqueKeysSet;
ResultSet uniqueKeysResultSet;
String uniqueKeyColumnName;
tablesList=new ArrayList<>();
while(tablesResultSet.next())
{
tableName=tablesResultSet.getString("TABLE_NAME");
if(tableName!=null) tableName=tableName.trim();
if(tableName==null) continue;
primaryKeysSet=new HashSet<>();
primaryKeysResultSet=databaseMetaData.getPrimaryKeys(catalog,null,tableName);
while(primaryKeysResultSet.next())
{
primaryKeyColumnName=primaryKeysResultSet.getString("COLUMN_NAME");
if(primaryKeyColumnName!=null) primaryKeyColumnName=primaryKeyColumnName.trim();
primaryKeysSet.add(primaryKeyColumnName);
}
primaryKeysResultSet.close();
uniqueKeysSet=new HashSet<>();
uniqueKeysResultSet=databaseMetaData.getIndexInfo(catalog,null,tableName,true,false);
while(uniqueKeysResultSet.next())
{
uniqueKeyColumnName=uniqueKeysResultSet.getString("COLUMN_NAME");
if(uniqueKeyColumnName!=null) uniqueKeyColumnName=uniqueKeyColumnName.trim();
uniqueKeysSet.add(uniqueKeyColumnName);
}
uniqueKeysResultSet.close();
table=new Table();
setField(table,"name",tableName);
tableColumns=new ArrayList<>();
ResultSet columnResultSet=databaseMetaData.getColumns(catalog,null,tableName,"%");
while(columnResultSet.next())
{
String columnName=columnResultSet.getString("COLUMN_NAME");
if(columnName!=null) columnName=columnName.trim();
TableColumn tableColumn=new TableColumn();
setField(tableColumn,"name",columnName);
setField(tableColumn,"type",columnResultSet.getString("TYPE_NAME"));
setField(tableColumn,"width",columnResultSet.getInt("COLUMN_SIZE"));
setField(tableColumn,"precision",columnResultSet.getInt("DECIMAL_DIGITS"));
setField(tableColumn,"isPrimaryKey",primaryKeysSet.contains(columnName));
setField(tableColumn,"isUnique",uniqueKeysSet.contains(columnName));
setField(tableColumn,"isNullable",columnResultSet.getInt("NULLABLE")==DatabaseMetaData.columnNullable);
setField(tableColumn,"defaultValue",columnResultSet.getString("COLUMN_DEF"));
setField(tableColumn,"isAutoIncrement","YES".equalsIgnoreCase(columnResultSet.getString("IS_AUTOINCREMENT")));
tableColumns.add(tableColumn);
}
columnResultSet.close();
setField(table,"tableColumns",tableColumns);
tablesList.add(table);
}
tablesResultSet.close();
setField(database,"tables",tablesList);
}catch(SQLException sqlException)
{
throw new DatabaseException("Unable to read database metadata: "+sqlException.getMessage());
}
return database;
}
private static void setField(Object object,String fieldName,Object value) throws DatabaseException
{
try
{
Field field=object.getClass().getDeclaredField(fieldName);
field.setAccessible(true);
field.set(object,value);
}catch(Exception exception)
{
throw new DatabaseException("Unable to set field "+fieldName+": "+exception.getMessage());
}
}
}
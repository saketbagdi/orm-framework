package com.orm.generator;
import com.orm.exceptions.*;
import java.io.*;
import java.util.*;
import com.orm.db.*;
public class DTOGenerator
{
private DTOGenerator(){}
private static Map<String,String> javaTypes=new HashMap<>();
private static Map<String,String> defaultValues=new HashMap<>();
static
{
javaTypes.put("DATETIME","java.sql.Timestamp");
javaTypes.put("TIMESTAMP","java.sql.Timestamp");
javaTypes.put("DATE","java.sql.Date");
javaTypes.put("TIME","java.sql.Time");
javaTypes.put("YEAR","short");
javaTypes.put("CHAR","String");
javaTypes.put("INT","int");
javaTypes.put("INTEGER","int");
javaTypes.put("SMALLINT","int");
javaTypes.put("TINYINT","byte");
javaTypes.put("MEDIUMINT","int");
javaTypes.put("BIGINT","long");
javaTypes.put("DECIMAL","java.math.BigDecimal");
javaTypes.put("NEUMERIC","java.math.BigDecimal");
javaTypes.put("FLOAT","float");
javaTypes.put("DOUBLE","double");
javaTypes.put("BIT","boolean");

defaultValues.put("DATETIME","null");
defaultValues.put("TIMESTAMP","null");
defaultValues.put("DATE","null");
defaultValues.put("TIME","null");
defaultValues.put("YEAR","0");
defaultValues.put("CHAR","\"\"");
defaultValues.put("INT","0");
defaultValues.put("INTEGER","0");
defaultValues.put("SMALLINT","0");
defaultValues.put("TINYINT","0");
defaultValues.put("MEDIUMINT","0");
defaultValues.put("BIGINT","0");
defaultValues.put("DECIMAL","0.0");
defaultValues.put("NEUMERIC","0.0");
defaultValues.put("FLOAT","0.0f");
defaultValues.put("DOUBLE","0.0");
defaultValues.put("BIT","false");
}

public static void generate(Database database, File directoryPath, String packageName) throws DatabaseException, IOException
{
if(directoryPath==null) directoryPath=new File(".");
if(directoryPath.exists()==false) directoryPath.mkdir();
String packagePath="";
if(packageName==null || packageName.length()==0) packagePath="";
else packagePath=packageName.replace(".",File.separator);
String directoryPathString=directoryPath.getAbsolutePath();
if(directoryPathString.endsWith(String.valueOf(File.separator)))
{
if(packagePath.length()>0) directoryPathString=directoryPathString+packagePath;
}
else
{
if(packagePath.length()>0) directoryPathString=directoryPathString+File.separator+packagePath;
}
File finalDirectoryPath=new File(directoryPathString);
if(finalDirectoryPath.exists()==false)
{
finalDirectoryPath.mkdir();
}
if(database==null)
{
throw new DatabaseException("database required");
}
List<Table>tables=database.getTables();
for(Table t: tables)
{
createTableDTO(t,finalDirectoryPath,packageName);
}
}

public static void createTableDTO(Table table, File directory,String packageName) throws IOException, DatabaseException
{
int index;
String tableName=table.getName();
List<TableColumn>tableColumns=table.getTableColumns();
String dtoName=getDTOName(tableName);
String dtoPointerName;
String propertyName;
String propertyType;
String line;
String pkName;
String pkType;
Vector<String> propertyNames=new Vector<>();
Vector<String> propertyTypes=new Vector<>();
String filePath=directory.getAbsolutePath();
if(filePath.endsWith(String.valueOf(File.separator))==false) filePath=filePath+File.separator;
filePath=filePath + dtoName + ".java";
File javaFile=new File(filePath);
if(javaFile.exists()) javaFile.delete();
String defaultValue;
RandomAccessFile randomAccessFile;
randomAccessFile=new RandomAccessFile(javaFile,"rw");
if(packageName!=null && packageName.length()>0)
{
randomAccessFile.writeBytes("package "+packageName+";\n");
}
randomAccessFile.writeBytes("import com.orm.annotation.*;\n");
Vector<String> primaryKeys=new Vector<>();
Vector<String> primaryKeyTypes=new Vector<>();
Vector<String> primaryKeysPropertyNames=new Vector<>();
for(TableColumn tableColumn: tableColumns)
{
if(tableColumn.getIsPrimaryKey())
{
primaryKeys.add(tableColumn.getName());
primaryKeyTypes.add(getPropertyType(tableColumn.getType()));
primaryKeysPropertyNames.add(getPropertyName(tableColumn.getName()));
}
}
if(primaryKeys.size()>1)
{
randomAccessFile.writeBytes("@CompositeKey(columnName={");
randomAccessFile.writeBytes("\""+primaryKeys.get(0)+"\"");
for(int idx=1;idx<primaryKeys.size();++idx)
{
randomAccessFile.writeBytes(",\""+primaryKeys.get(idx)+"\"");
}
randomAccessFile.writeBytes("})\n");
}
randomAccessFile.writeBytes("@Table(name=\""+tableName+"\")\n");
randomAccessFile.writeBytes("public class "+dtoName+" implements java.io.Serializable \n");
randomAccessFile.writeBytes("{\n");
for(TableColumn t:tableColumns)
{
propertyName=getPropertyName(t.getName());
propertyType=getPropertyType(t.getType());
propertyNames.add(propertyName);
propertyTypes.add(propertyType);
randomAccessFile.writeBytes("private "+getPropertyType(t.getType())+" "+propertyName+";\n");
}
//constructor starts
randomAccessFile.writeBytes("public "+dtoName+"()\n");
randomAccessFile.writeBytes("{\n");
for(TableColumn tableColumn:tableColumns)
{
propertyName=getPropertyName(tableColumn.getName());
defaultValue=defaultValues.get(tableColumn.getType().toUpperCase());
randomAccessFile.writeBytes("this."+propertyName+"="+defaultValue+";\n");
}
randomAccessFile.writeBytes("}\n");
//constructor ends

//parameterized constructor with properties that are not auto incrementable
int autoIncrementablePropertiesCount=0;
for(TableColumn tableColumn:tableColumns)
{
if(tableColumn.getIsAutoIncrement()) autoIncrementablePropertiesCount++;
}
if(tableColumns.size()>autoIncrementablePropertiesCount)
{
randomAccessFile.writeBytes("public "+dtoName+"(");
index=0;
int parameterIndex=0;
while(index<propertyNames.size())
{
if(tableColumns.get(index).getIsAutoIncrement()==false)
{
if(parameterIndex>0) randomAccessFile.writeBytes(",");
randomAccessFile.writeBytes(propertyTypes.get(index)+" "+propertyNames.get(index));
parameterIndex++;
}
++index;
}
randomAccessFile.writeBytes(")\n");
randomAccessFile.writeBytes("{\n");
for(index=0;index<tableColumns.size();++index)
{
propertyName=propertyNames.get(index);
if(tableColumns.get(index).getIsAutoIncrement()==false)
{
randomAccessFile.writeBytes("this."+propertyName+"="+propertyName+";\n");
}
else
{
defaultValue=defaultValues.get(tableColumns.get(index).getType().toUpperCase());
randomAccessFile.writeBytes("this."+propertyName+"="+defaultValue+";\n");
}
}
randomAccessFile.writeBytes("}\n");
}

//compareTo starts
dtoPointerName=getDTOPointerName(dtoName);
randomAccessFile.writeBytes("public int compareTo("+dtoName+" "+dtoPointerName+")\n");
randomAccessFile.writeBytes("{\n");
index=0;
line="int ret;\n";
randomAccessFile.writeBytes(line);
while(index<primaryKeys.size())
{
pkName=primaryKeysPropertyNames.get(index);
pkType=primaryKeyTypes.get(index);
if(isPrimitive(pkType))
{
if(pkType.equalsIgnoreCase("boolean"))
{
line="if(this."+pkName+"=="+dtoPointerName+"."+pkName+")ret=0;\n";
randomAccessFile.writeBytes(line);
line="else if(this."+pkName+") ret=1;\n";
randomAccessFile.writeBytes(line);
line="else ret=-1;\n";
randomAccessFile.writeBytes(line);
}
else
{
line="ret=this."+pkName+"-"+dtoPointerName+"."+pkName+";\n";
randomAccessFile.writeBytes(line);
}
}
else
{
line="ret=this."+pkName+".compareTo("+dtoPointerName+"."+pkName+");\n";
randomAccessFile.writeBytes(line);
}
if(index<primaryKeysPropertyNames.size()-1)
{
line="if(ret!=0) return ret;\n";
randomAccessFile.writeBytes(line);
}
++index;
}
line="return ret;\n";
randomAccessFile.writeBytes(line);
randomAccessFile.writeBytes("}\n");
//compareTo ends

//equals starts
dtoPointerName=getDTOPointerName(dtoName);
randomAccessFile.writeBytes("public boolean equals("+dtoName+" "+dtoPointerName+")\n");
randomAccessFile.writeBytes("{\n");
randomAccessFile.writeBytes("return ");
index=0;
while(index<primaryKeys.size())
{
propertyName=primaryKeysPropertyNames.get(index);
propertyType=primaryKeyTypes.get(index);
if(isPrimitive(propertyType))
{
randomAccessFile.writeBytes("this."+propertyName+"=="+dtoPointerName+"."+propertyName);
}
else
{
randomAccessFile.writeBytes("this."+propertyName+".equals("+dtoPointerName+"."+propertyName+")");
}
if(index<primaryKeys.size()-1)randomAccessFile.writeBytes("&&");
++index;
}
randomAccessFile.writeBytes(";\n");
randomAccessFile.writeBytes("}\n");
//equals ends

//hashCode starts
randomAccessFile.writeBytes("public int  hashCode()\n");
randomAccessFile.writeBytes("{\n");
randomAccessFile.writeBytes("}\n");
//hashCode ends

//setters and getters Starts
index=0;
for(String prop:propertyNames)
{
line="public void "+getSetterName(prop)+"("+propertyTypes.get(index)+" "+prop+")\n";
randomAccessFile.writeBytes(line);
line="{\n";
randomAccessFile.writeBytes(line);
line="this."+prop+"="+prop+";\n";
randomAccessFile.writeBytes(line);
line="}\n";
randomAccessFile.writeBytes(line);
line="public "+propertyTypes.get(index)+" "+getGetterName(prop)+"()\n";
randomAccessFile.writeBytes(line);
line="{\n";
randomAccessFile.writeBytes(line);
line="return this."+prop+";\n";
randomAccessFile.writeBytes(line);
line="}\n";
randomAccessFile.writeBytes(line);
++index;
}
//setters and getters ends

randomAccessFile.writeBytes("}\n");
randomAccessFile.close();
}

public static String getPropertyType(String tableColumnType)
{
if(tableColumnType==null) return null;
String type=tableColumnType.toUpperCase();
String result=javaTypes.get(type);
if(result==null) result="String";
return result;
}

public static String getPropertyName(String property)
{
if(property==null || property.length()==0) return property;
String splits[]=property.split("_");
String propertyName=splits[0].toLowerCase();
for(int idx=1;idx<splits.length;++idx)
{
if(splits[idx].length()>0)
{
propertyName+=splits[idx].substring(0,1).toUpperCase()+splits[idx].substring(1).toLowerCase();
}
}
return propertyName;
}

public static String getDTOName(String tableName)
{
String splits[]=tableName.split("_");
int idx=0;
while(idx<splits.length && splits[idx].length()==0)
{
++idx;
}
if(idx>=splits.length) return "DTO";
String dtoName=splits[idx].substring(0,1).toUpperCase()+splits[idx].substring(1).toLowerCase();
for(++idx;idx<splits.length;++idx)
{
if(splits[idx].length()>0)
{
dtoName+=splits[idx].substring(0,1).toUpperCase()+splits[idx].substring(1).toLowerCase();
}
}
return dtoName;
}

public static String getDTOPointerName(String dtoName)
{
return Character.toLowerCase(dtoName.charAt(0))+dtoName.substring(1);
}

public static boolean isPrimitive(String propertyType)
{
return propertyType.equals("byte") ||
propertyType.equals("short") ||
propertyType.equals("int") ||
propertyType.equals("long") ||
propertyType.equals("float") ||
propertyType.equals("double") ||
propertyType.equals("boolean") ||
propertyType.equals("char");
}

public static String getSetterName(String property)
{
return "set"+Character.toUpperCase(property.charAt(0))+property.substring(1);
}

public static String getGetterName(String property)
{
return "get"+Character.toUpperCase(property.charAt(0))+property.substring(1);
}
}
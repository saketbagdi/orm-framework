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

defaultValues.put("java.sql.Timestamp","null");
//defaultValues.put("java.sql.Timestamp","null");
defaultValues.put("java.sql.Date","null");
defaultValues.put("java.sql.Time","null");
defaultValues.put("short","0");
defaultValues.put("String","\"\"");
defaultValues.put("int","0");
//defaultValues.put("int","0");
//defaultValues.put("int","0");
defaultValues.put("byte","0");
//defaultValues.put("int","0");
defaultValues.put("long","0");
defaultValues.put("java.math.BigDecimal","0.0");
//defaultValues.put("java.math.BigDecimal","0.0");
defaultValues.put("float","0.0f");
defaultValues.put("double","0.0");
defaultValues.put("boolean","false");
}

public static void generate(Database database, File directoryPath, String packageName) throws DatabaseException
{
try
{
if(database==null)throw new DatabaseException("Datbase required");
if(directoryPath==null) directoryPath=new File(".");
if(packageName==null || packageName.length()==0) packageName="";
if(directoryPath.exists()==false)
{
if(directoryPath.mkdir()==false)
{
System.out.println("Couldn't create " +directoryPath.getAbsolutePath());
}
}
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
if(finalDirectoryPath.mkdirs()==false)
{
System.out.println("Couldn't create "+directoryPath.getAbsolutePath());
}
}
List<Table>tables=database.getTables();
for(Table t: tables) createTableDTO(t,finalDirectoryPath,packageName);
}catch(IOException ioException)
{
throw new DatabaseException(ioException.getMessage());
}
}

static public String getPropertyType(String tableColumnType)
{
return javaTypes.get(tableColumnType.toUpperCase());
}
static public String getDTOPointerName(String dtoName)
{
String dtoPointerName=dtoName.substring(0,1).toLowerCase();
if(dtoName.length()>0) dtoPointerName+=dtoName.substring(1);
return dtoPointerName;
}
static public boolean isPrimitive(String javaType)
{
if(javaType.equals("long")) return true;
if(javaType.equals("int")) return true;
if(javaType.equals("short")) return true;
if(javaType.equals("byte")) return true;
if(javaType.equals("double")) return true;
if(javaType.equals("float")) return true;
if(javaType.equals("char")) return true;
if(javaType.equals("boolean")) return true;
return false;
}
static public String getPropertyName(String property)
{
String [] splits=property.split("_");
for(String s:splits)s=s.trim();
int idx=0;
while(splits[idx].length()==0) ++idx;
String propertyName=splits[idx].toLowerCase();
for(++idx;idx<splits.length;++idx)
{
if(splits[idx].length()>0) propertyName+=(splits[idx].substring(0,1).toUpperCase()+splits[idx].substring(1).toLowerCase());
}
return propertyName;
}
static public String getDTOName(String tableName)
{
String [] splits=tableName.split("_");
for(String s:splits)s=s.trim();
int idx=0;
while(splits[idx].length()==0) ++idx;
String  dtoName=splits[idx].substring(0,1).toUpperCase()+splits[idx].substring(1).toLowerCase();
for(++idx;idx<splits.length;++idx)
{
if(splits[idx].length()>0) dtoName+=(splits[idx].substring(0,1).toUpperCase()+splits[idx].substring(1).toLowerCase());
}
return dtoName;
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
Vector<String>primaryKeysPropertyTypes=new Vector<>();
String line;
String pkName;
String pkType;
Map<String,ParentColumn> parentColumnsMap;
ParentColumn parentColumn;
String foreignKeyName;
int keySequence;
String parentTableName;
String parentTableColumnName;
String childTableColumnName;
Map<String,String> foreignKeyNamesMap;
foreignKeyNamesMap=table.getForeignKeyNamesMap();
Map<String, ForeignKeyColumnMappings> foreignKeyColumnMappingsMap;
foreignKeyColumnMappingsMap=table.getForeignKeyColumnMappingMap();
for(TableColumn tableColumn: tableColumns)
{
if(tableColumn.getIsPrimaryKey())
{
primaryKeys.add(tableColumn.getName());
primaryKeyTypes.add(getPropertyType(tableColumn.getType()));
//primaryKeysPropertyNames.add(getPropertyName(tableColumn.getName()));
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
randomAccessFile.writeBytes("public class "+dtoName+" implements java.io.Serializable, Comparable<"+ dtoName +">\n");
randomAccessFile.writeBytes("{\n");
ForeignKeyColumnMappings foreignKeyColumnMappings;
for(TableColumn t:tableColumns)
{
//t is tableColumn Pointer
foreignKeyName=foreignKeyNamesMap.get(t.getName());
if(foreignKeyName==null)
{
propertyName=getPropertyName(t.getName());
propertyType=getPropertyType(t.getType());
propertyNames.add(propertyName);
propertyTypes.add(propertyType);
if(t.getIsAutoIncrement())
{
randomAccessFile.writeBytes("@Auto\n");
}
if(t.getIsPrimaryKey())
{
randomAccessFile.writeBytes("@PrimaryKey\n");
primaryKeysPropertyNames.add(propertyName);
primaryKeysPropertyTypes.add(propertyType);
}
if(t.getIsUnique() && !t.getIsPrimaryKey())
{
randomAccessFile.writeBytes("@Unique\n");
}
if(t.getIsNullable()==false)
{
randomAccessFile.writeBytes("@NotNull\n");
}
randomAccessFile.writeBytes("@MaxLength("+t.getWidth()+")\n");
randomAccessFile.writeBytes("@Column(name=\""+t.getName()+";\n");
randomAccessFile.writeBytes("private "+propertyType+" "+propertyName+";\n");
}
else
{
foreignKeyColumnMappings=foreignKeyColumnMappingsMap.get(foreignKeyName);
parentColumnsMap=foreignKeyColumnMappings.getParentColumnsMap();
parentColumn=parentColumnsMap.get(t.getName());
if(parentColumnsMap.size()>1) continue; //because generating Composite Foreign key is still left
if(parentColumn.getKeySequence()!=1)continue;
parentTableName=foreignKeyColumnMappings.getParentTableName();
propertyType=getDTOName(parentTableName);
propertyName=getDTOPointerName(propertyType);
primaryKeysPropertyNames.add(propertyName);
primaryKeysPropertyTypes.add(propertyType);
randomAccessFile.writeBytes("@ForeignKey(column=\""+t.getName()+"\",parentColumn=\""+parentColumn.getName()+"\")\n");
randomAccessFile.writeBytes("private "+propertyType+" "+propertyName+";\n");
propertyNames.add(propertyName);
propertyTypes.add(propertyType);
}
}
//constructor starts
randomAccessFile.writeBytes("public "+dtoName+"()\n");
randomAccessFile.writeBytes("{\n");
for(index=0;index<propertyNames.size();++index)
{
propertyName = propertyNames.get(index);
propertyType=propertyTypes.get(index);
defaultValue=defaultValues.get(propertyType);
if(defaultValue==null)
{
randomAccessFile.writeBytes("this."+propertyName+"=null;\n");
}
else
{
randomAccessFile.writeBytes("this."+propertyName+"="+defaultValue+";\n");
}
}
randomAccessFile.writeBytes("}\n");
//constructor ends

//parameterized constructor with properties that are not auto incrementable
randomAccessFile.writeBytes("public  "+dtoName+"(");
randomAccessFile.writeBytes(propertyTypes.get(0)+" "+propertyNames.get(0));
index=1;
while(index<propertyNames.size())
{
randomAccessFile.writeBytes(","+propertyTypes.get(index)+" "+propertyNames.get(index));
++index;
}
randomAccessFile.writeBytes(")\n");
randomAccessFile.writeBytes("{\n");
for(String propName:propertyNames)
{
randomAccessFile.writeBytes("this."+propName+"="+propName+";\n");
}
randomAccessFile.writeBytes("}\n");
// parameterized constructor with properties that are not auto incrementable
int autoIncrementablePropertiesCount=0;
for(TableColumn tableColumn:tableColumns)
{
if(tableColumn.getIsAutoIncrement()) autoIncrementablePropertiesCount++;
}
if(autoIncrementablePropertiesCount>0 && tableColumns.size()>autoIncrementablePropertiesCount)
{
randomAccessFile.writeBytes("public  "+dtoName+"(");
index=0;
while(index<propertyNames.size())
{
if(tableColumns.get(index).getIsAutoIncrement()==false)
{
randomAccessFile.writeBytes(propertyTypes.get(index)+" "+propertyNames.get(index));
if(index<tableColumns.size()-1) randomAccessFile.writeBytes(",");
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
randomAccessFile.writeBytes("this."+propertyName+"="+defaultValues.get(propertyTypes.get(index))+";\n");
}
}
randomAccessFile.writeBytes("}\n");
}

//setters and getters Starts
index=0;
for(String  prop:propertyNames)
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
pkType=primaryKeysPropertyTypes.get(index);
if(isPrimitive(pkType))
{
if(pkType.equalsIgnoreCase("boolean"))
{
line="if(this."+pkName+"=="+dtoPointerName+"."+pkName+") ret=0;\n";
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
// hashCode starts
randomAccessFile.writeBytes("public int hashCode()\n");
randomAccessFile.writeBytes("{\n");
randomAccessFile.writeBytes("return  java.util.Objects.hash(");
index=0;
while(index<primaryKeys.size())
{
if(index<primaryKeys.size()-1)
{
randomAccessFile.writeBytes(primaryKeysPropertyNames.get(index)+",");
}
else
{
randomAccessFile.writeBytes(primaryKeysPropertyNames.get(index));
}
index++;
}
randomAccessFile.writeBytes(");\n");
randomAccessFile.writeBytes("}\n");
//hashCode ends
randomAccessFile.writeBytes("}\n");
randomAccessFile.close();
}

public static String getSetterName(String propertyName)
{
String str="set" +propertyName.substring(0,1).toUpperCase();
if(propertyName.length()>1)
{
str+=propertyName.substring(1);
}
return str;
}

public static String getGetterName(String propertyName)
{
String str="get" +propertyName.substring(0,1).toUpperCase();
if(propertyName.length()>1)
{
str+=propertyName.substring(1);
}
return str;
}
}
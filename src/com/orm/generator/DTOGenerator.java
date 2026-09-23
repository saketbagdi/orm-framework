package com.orm.generator;
import com.orm.exceptions.*;
import java.io.*;
import java.util.*;
import com.orm.db.*;
public class DTOGenerator
{
private DTOGenerator(){}
public static void generate(Database database, File directoryPath, String packageName) throws DatabaseException, IOException
{
if(directoryPath==null) directoryPath=new File(".");
if(directoryPath.exists()==false) directoryPath.mkdir();
String packagePath="";
if(packageName==null || packagePath.length()==0)
{
packagePath="";
}
else
{
packagePath=packageName.replace(".",File.separator);
}
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
if(finalDirectoryPath.exists()== false)
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
//throw new DatabaseException("Not yet implemented");
}
public static void createTableDTO(Table table, File directory,String packageName) throws IOException, DatabaseException
{
String tableName=table.getName();
String className=getDTOName(tableName);

String filePath=directory.getAbsolutePath();
if(filePath.endsWith(File.separator)==false)
{
filePath=filePath+File.separator;
}
filePath=filePath+className+".java";

File javaFile=new File(filePath);
RandomAccessFile randomAccessFile=new RandomAccessFile(javaFile,"rw");
if(packageName!=null && packageName.length()>0)
{
randomAccessFile.writeBytes("package "+packageName+";\n");
}
randomAccessFile.writeBytes("import com.orm.annotation.*;\n");
randomAccessFile.writeBytes("public class "+className+" implements java.io.Serializable \n");
randomAccessFile.writeBytes("{\n");
randomAccessFile.writeBytes("}\n");
randomAccessFile.close();
}
public static String getDTOName(String tableName)
{
String splits[]=tableName.split("_");
int idx=0;
while(splits[idx].length()==0)
{
++idx;
}
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
}
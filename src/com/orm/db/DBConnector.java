package com.orm.db;
import java.sql.*;
import java.io.*;
import java.util.*;
public class DBConnector
{
public static Connection connect()
{
Connection connection=null;
try
{
File file=new File("db.conf");
if(file.exists()==false) throw new Exception("db.conf missing");
RandomAccessFile randomAccessFile;
randomAccessFile=new RandomAccessFile(file,"rw");
if(randomAccessFile.length()==0)
{
randomAccessFile.close();
throw new Exception("db.conf is empty");
}
String line;
String splits[];
Map<String,String> keyValues=new HashMap<>();
while(randomAccessFile.getFilePointer()<randomAccessFile.length())
{
line= randomAccessFile.readLine();
splits=line.split("=");
if(splits.length==2)
{
keyValues.put(splits[0].trim().toUpperCase().splits[1].trim());
}
}
randomAccessFile.close();
String driver=keyValues.get("DRIVER");
if(driver==null)throw new Exception("driver missing in db.conf");
String username=keyValues.get("USERNAME");
if(username==null)throw new Exception("username missing in db.conf");
String password=keyValues.get("PASSWORD");
if(password==null)throw new Exception("password missing in db.conf");
String database=keyValues.get("DATABASE");
if(database==null)throw new Exception("database missing in db.conf");
String protocol=keyValues.get("PROTOCOL");
if(protocol==null)throw new Exception("protocol missing in db.conf");
String port=keyValues.get("PORT");
if(port==null)throw new Exception("port missing in db.conf");
String server=keyValues.get("SERVER");
if(server==null)throw new Exception("server missing in db.conf");
Class.forName(driver);
connection=DriverManager.getConnection(protocol+"://"+server+":"+port+"/"+database,username,password);
}catch(Eception exception)
{
System.out.println(exception);
System.exit(0);
}
return connection;
}
}
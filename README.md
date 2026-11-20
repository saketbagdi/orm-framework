# ORM Framework

A lightweight Java project that reads a relational database schema through JDBC metadata and generates annotated Java DTO classes from it. It is the foundation for a from-scratch ORM built without Hibernate or JPA.

> **Status: early development.** Schema introspection and DTO generation are implemented. Query execution (CRUD), foreign-key discovery, and annotation validation are not yet. See [Current status](#current-status) and the [Roadmap](#roadmap).

## What it does

1. **Connects** to a database using a `db.conf` file (no credentials in code).
2. **Reads the schema** with `DatabaseMetaData` and builds an in-memory model: tables, columns, types, sizes, precision, primary keys, unique columns, nullability, defaults and auto-increment flags.
3. **Generates DTO classes** (one per table) as `.java` source files, annotated with mapping and constraint annotations, with constructors, getters/setters, `compareTo`, `equals` and `hashCode`.

## Current status

| Area | Status |
|---|---|
| Connection via `db.conf` (`DBConnector`) | Implemented |
| Schema model (`Database`, `Table`, `TableColumn`, `ForeignKey`, `ForeignKeyColumnMapping`) | Implemented |
| Reading tables, columns, primary keys, unique columns, nullability, defaults, auto-increment (`DatabaseFactory`) | Implemented |
| DTO generation with constructors, accessors, `compareTo`, `equals`, `hashCode` (`DTOGenerator`) | Implemented, still being refined |
| Annotation definitions (mapping, constraints, validation) | Defined |
| Reading foreign keys from the database | Not yet |
| Runtime enforcement of the validation annotations | Not yet |
| CRUD / query execution | Not yet |
| Automated tests | Not yet |

## Project structure

```
src/com/orm
├── annotation/    @Table @Column @PrimaryKey @ForeignKey @CompositeKey @CompositeForeignKey
│                  @Auto @NotNull @Unique @MaxLength
│                  @CheckEqualTo @CheckNotEqualTo @CheckGreaterThan @CheckGreaterThanEqualTo
│                  @CheckLessThan @CheckLessThanEqualTo @CheckValueIn @CheckPrecision
├── db/            DBConnector, DatabaseFactory, Database, Table, TableColumn,
│                  ForeignKey, ForeignKeyColumnMapping
├── exceptions/    DatabaseException
└── generator/     DTOGenerator
testcases/         Sample entities (Department, Employee) and Windows build/run scripts
```

## Requirements

- Java 8 or newer
- MySQL and the MySQL Connector/J JDBC driver on the classpath

## Configuration

Create a `db.conf` file in the directory you run from. **Do not commit it**; it contains credentials. Add `db.conf` to `.gitignore`.

```
driver=com.mysql.cj.jdbc.Driver
protocol=jdbc:mysql
server=localhost
port=3306
database=your_database
username=your_user
password=your_password
```

The connection URL is built as `protocol://server:port/database`.

## Build

From the repository root on Windows:

```bat
dir /s /b src\*.java > sources.txt
javac -d classes @sources.txt
```

On Linux or macOS:

```bash
javac -d classes $(find src -name "*.java")
```

## Usage

```java
import com.orm.db.*;
import com.orm.generator.DTOGenerator;
import java.io.File;
import java.sql.Connection;

public class Demo
{
    public static void main(String[] args) throws Exception
    {
        Connection connection = DBConnector.connect();          // reads db.conf
        Database database = DatabaseFactory.getDatabase(connection); // reads schema metadata
        DTOGenerator.generate(database, new File("dto"), "com.example.dto");
        connection.close();
    }
}
```

Run it with the JDBC driver on the classpath:

```bash
java -cp "classes:lib/*:." Demo
```

This writes one DTO class per table into `dto/com/example/dto/`. A generated DTO looks like this (annotations vary by column):

```java
@Table(name="department")
public class DepartmentDTO implements java.io.Serializable, Comparable<DepartmentDTO>
{
    @Auto
    @PrimaryKey
    private int code;

    @NotNull
    @Unique
    @MaxLength(50)
    private String name;

    // constructors, getters/setters, compareTo, equals, hashCode
}
```

*Replace this sample with real output from your own run once generation works.*

## Roadmap

- [ ] Read foreign keys (single and composite) via `getImportedKeys`
- [ ] Enforce the `@Check*`, `@NotNull`, `@MaxLength` annotations at runtime using reflection
- [ ] INSERT / SELECT / UPDATE / DELETE using `PreparedStatement`
- [ ] Maven or Gradle build
- [ ] JUnit tests
- [ ] Connection pooling

## Author

**Saket Bagdi**: [GitHub](https://github.com/saketbagdi) · saket.bagdi@gmail.com

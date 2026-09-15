@Table(name="department")
public class Department
{
@Auto
@PrimaryKey
private int code;
@NotNull
@Unique
@MaxLength(50)
private String name;
/*
constructor: default
constructor: parameterized
setter/getter
equals
compareTo
hashCode
toString
*/
}
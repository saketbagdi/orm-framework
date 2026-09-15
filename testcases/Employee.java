@Table(name="employee")
public class Employee
{
@MaxLength(15)
@PrimaryKey
@Column(name="emp_id")
private Stirng empId;
@NotNull
@MaxLength(50)
@Column
private String name;
@NotNull
@Column(name="department_cod")
@ForeignKey
private Department department;
@NotNull
@Column(name="is_indian")
private boolean isIndian;
@NotNull
@Column(name="date_of_birth")
private java.sql.Date dateOfBirth;
@NotNull
@CheckString(list={"M","m","F","f"})
@Column
private String gender;
@NotNull
@Column(name="gross_salary")
@CheckGreaterThan(0.0)
@CheckPrecision("6.2")
private java.math.BigDecimal grossSalary;
}
/*
8 more like CheckString
+1 more for BigDecimal like CheckString
Following 6 for BigDecimal and  6 for long,int,short,byte,double,float
CheckGreaterThan 
CheckGreaterEqualTo
CheckLessThan
CheckLessThanEqualTo
CheckNotEqualTo
CheckEqualTo
*/

package hellooo;
import java.util.Scanner;

public class Student
{
    Integer id;
    String name;
    String gender;

    public Student(int i,String n,String g)
    {
        this.id=i;
        this.gender=g;
        this.name=n;
    }

    public void setName(String n)
    {
        name=n;
    }
    public String getName()
    {
        return name;
    }
    public void setId(int i)
    {
        id=i;
    }
    public Integer getId()
    {
        return id;
    }
    public void setGender(String g)
    {
        gender=g;
    }
    public String getGender()
    {
        return gender;
    }
    public void greet()
    {
        String ss="Class 3";
        System.out.println("Hello,I'm "+name+",I am a "+gender+" student,"+"and I am from Class 3");
        System.out.println("Hello,I'm "+getName()+",I am a "+getGender()+" student,"+ "and I am from "+ss);
        System.out.println(ss.charAt(6));
    }
    public static boolean isDigit(String input) {

        // null or length < 0, return false.
        if (input == null || input.length() < 0)
            return false;

        // empty, return false
        input = input.trim();
        if ("".equals(input))
            return false;

        if (input.startsWith("-")) {
            // negative number in string, cut the first char
            return input.substring(1).matches("[0-9]*");
        } else {
            // positive number, good, just check
            return input.matches("[0-9]*");
        }
    }
    public static void main(String [] args )
    {
        Student su1=new Student(1,"Xiang zhang","male");

        Scanner scan = new Scanner(System.in);
        if (scan.hasNextLine()) {
            String str2 = scan.nextLine();
            if (isDigit(str2)) {
                Integer result = Integer.parseInt(str2);;
                Student su3=new Student(result,args[1],args[2]);
                su3.greet();
            } else {
                System.out.println("Please provide a valid digit [0-9]");
            }
            System.out.println("输入的数据为：" + str2);
        }


        if (isDigit(args[0])) {
            Integer result = Integer.parseInt(args[0]);;
            Student su2=new Student(result,args[1],args[2]);
            su2.greet();
        } else {
            System.out.println("Please provide a valid digit [0-9]");
        }
        su1.greet();

    }


}

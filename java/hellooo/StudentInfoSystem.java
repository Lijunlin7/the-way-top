package hellooo;

import java.util.Objects;
import java.util.Scanner;

public class StudentInfoSystem
{
    private static int counter=0;
    static class  Student
    {
        private int id;
        private String name;
        private boolean gender;
        private int javascore;

        public Student(int i,String n,boolean g,int j)
        {
            this.id=i;
            this.name=n;
            this.gender=g;
            this.javascore=j;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isGender() {
            return gender;
        }

        public int getJavascore() {
            return javascore;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setGender(boolean gender) {
            this.gender = gender;
        }

        public void setJavascore(int javascore) {
            this.javascore = javascore;
        }
    }
    public static Student[] students;
    public StudentInfoSystem()
    {
        students=new Student[20];
        counter=0;
    }
    /*public boolean judge(int i,String n,boolean g,int j)
    {


        return false;
    }

     */
    public void setinformation(int i,String n,boolean g,int j) {
        /*if(judge( i, n, g, j))
        {

         */
//可行性验证 是否符合实际，是否重复
        if (n != null) {
            if (j >= 0 && j <= 100) {
                /*if(counter>0) {
                    for (int m = 1; m <= counter; m++) {
                        if (n.equals(students[m].getName()) || i == students[m].getId()) {
                            System.out.println("name or id has repeated,please enter again");
                            return false;
                        }
                    }
                }

                 */
                students[counter] = new Student(i, n, g, j);
                System.out.println("next");
                counter++;
            } else {
                System.out.println("Please enter the right score");
                Scanner s4 = new Scanner(System.in);
                int b4 = s4.nextInt();
                j = b4;
            }
            students[counter] = new Student(i, n, g, j);
            System.out.println("next");
            counter++;
        }
    }

    public int getCounter()
    {
        return counter;
    }
    public void sort(int l,int r)
    {
        if(l>=r)
        {
            return;
        }
        int i=l-1,j=r+1;
        Student mid=students[(l+r)/2];
        while(i<j)
        {
            do
            {
                i++;
            }while(students[i].javascore<mid.javascore);
            do
            {
                j--;
            }while(students[j].javascore>mid.javascore);
            if(i<j)
            {
                Student temp=students[i];
                students[i]=students[j];
                students[j]=temp;
            }
        }
        sort(l,j);
        sort(j+1,r);
    }

    public void showStudents()
    {
        sort(0,counter-1);
        for(int i=0;i<counter;i++)
        {
            System.out.println("id: "+students[i].getId()+"\nname: "+students[i].getName());
            if(students[i].isGender())
            {
                System.out.println("gender: "+"male");
            }
            else {
                System.out.println("gender: "+"female");
            }
            System.out.println("javascore: "+students[i].getJavascore());
        }
    }
    public void menu(StudentInfoSystem first,int num)
    {
        System.out.println("Please select your transaction:\n 1.Input\n 2.Output \n 3.getcounter \n 4.exit");
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        while(a!=4)
        {
            switch (a) {
                case 1:
                    for(int m=1;m<=num;m++)
                    {
                        System.out.println("Please enter the stundets'id");
                        Scanner s1 = new Scanner(System.in);
                        int b1 = s1.nextInt();
                        System.out.println("Please enter the stundets'name");
                        Scanner s2 = new Scanner(System.in);
                        String b2 = s2.next();
                        System.out.println("Please enter the stundets'gender(male or female)");
                        Scanner s3 = new Scanner(System.in);
                         String b3 = s3.next();
                        boolean gen;

                        gen= Objects.equals(b3, "male");

                        System.out.println("Please enter the stundets'javascore");
                        Scanner s4 = new Scanner(System.in);
                        int b4 = s4.nextInt();

                        first.setinformation(b1,b2,gen,b4);
                    }
                    break;
                case 2:
                    first.showStudents();//可选
                    break;
                //你可以有任意数量的case语句
                case 3:
                    System.out.println(first.getCounter());
                    break;
                default: //可选
                    System.out.println("Please enter the correct number");
                    break;
            }
            if(a==1||a==2||a==3)
            {
                System.out.println("Please select your transaction:\n 1.Input\n 2.Output \n 3.getcounter \n 4.exit");
                Scanner scanner2 = new Scanner(System.in);
                a= scanner2.nextInt();
            }
            if(a!=1&&a!=2&&a!=3&&a!=4)
            {
                break;
            }
        }
    }

    public static void main(String [] args)
    {

        System.out.println("Please enter the number you want");
        Scanner scanner = new Scanner(System.in);
        int num= scanner.nextInt();
        if(num<=0)
        {
            System.out.println("Please enter the correct number ");
        }
        StudentInfoSystem system1=new StudentInfoSystem();
        system1.menu(system1,num);

    }

}

package hellooo;
import java.util.Scanner;

public class ATM
{
    //String accountId;
    //int balan;
    //int secn;
    User one;
    public ATM(String a,int b) {
        one.UserId= a;
        one.balan = b;
        // this.secn=s;
    }
    public ATM(User g)
    {
        this.one=g;
    }


    public void Deposite()
    {
        System.out.println("Please enter the number you want to deposit");
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        while(a<=0)
        {
            System.out.println("Please enter the correct number you want to deposit");
            Scanner scanner1 = new Scanner(System.in);
            a=scanner1.nextInt();
        }
        one.balan+=a;
    }
    public void Withdraw()
    {
        System.out.println("Please enter the number you want to withdraw");
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        while(a<=0)
        {
            System.out.println("Please enter the correct number you want to withdraw");
            Scanner scanner1 = new Scanner(System.in);
            a=scanner1.nextInt();
        }
        while(a>one.balan)
        {
            System.out.println("balance is insufficient\nPlease enter the correct number");
            Scanner scanner1 = new Scanner(System.in);
            a=scanner1.nextInt();
        }
        one.balan-=a;
    }
    public void Query()
    {
        System.out.println("AccountID: "+one.UserId+"  "+"balance: "+one.balan);
    }
    public void event(ATM first)
    {
        System.out.println("Please select your transaction:\n 1.Deosite\n 2.Withdraw \n 3.Query \n 4.exit");
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        while(a!=4)
        {
            switch (a) {
                case 1:
                    first.Deposite();//可选
                    break;
                case 2:
                    first.Withdraw();//可选
                    break;
                //你可以有任意数量的case语句
                case 3:
                    first.Query();
                    break;
                default: //可选
                    System.out.println("Please enter the correct number");
                    break;
            }
            if(a==1||a==2||a==3)
            {
                System.out.println("Please select your transaction:\n 1.Deosite\n 2.Withdraw \n 3.Query \n 4.exit");
                Scanner scanner2 = new Scanner(System.in);
                a= scanner2.nextInt();
            }
            if(a!=4&&a!=1&&a!=2&&a!=3)
            {
                break;
            }
        }
    }


    public static void main(String [] args)
    {
        //ATM first =new ATM("tom",0);
        //读取账户
        User tt=new User();
        int result = Integer.parseInt(args[1]);
        tt.setBalan(result);
        tt.setUserId(args[0]);
        ATM M=new ATM(tt);
        M.event(M);
    }

}

package hellooo;

public class jjjj {
    private String name;
    private int age;
    public static int counter=0;

    public jjjj(String n,int a)
    {
        name=n;
        age=a;
        counter++;
    }
    protected void finalize()
    {
        counter--;
    }
    public void greet()
    {
        System.out.println("klmk");
    }
    public static void main(String [] args)
    {
        for(int i=0;i<10;i++)
        {
            jjjj tom=new jjjj("tom",18);
            tom.greet();
        }
        System.out.println(jjjj.counter);
    }



}

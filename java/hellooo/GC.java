package hellooo;

import java.util.ArrayList;

public class GC
{
    public static  void main(String []args)
    {
        System.gc();
        System.out.println("memery: "+Runtime.getRuntime().freeMemory());
        System.out.println("Creating houses...");
        ArrayList<House>area=new ArrayList<House>();
        for(int i=0;i<10;i++)
        {
            area.add(new House());
        }
        System.out.println("Memoery:"+Runtime.getRuntime().freeMemory());
        System.out.println("Collecting garbage...");
        System.gc();
        System.out.println("Memoery:"+Runtime.getRuntime().freeMemory());
    }

}

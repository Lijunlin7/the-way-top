package hellooo;

public class User
{
    String UserId;
    int balan;
    public User(String i , int b)
    {
        this.UserId=i;
        this.balan=b;
    }
    public User()
    {
        this.UserId="0";
        this.balan=0;
    }

    public void setUserId(String i)
    {
        UserId=i;
    }
    public String getUserId()
    {
        return UserId;
    }

    public void setBalan(int balan)
    {
        this.balan = balan;
    }

    public int getBalan()
    {
        return balan;
    }
}

#include <iostream>
using namespace std;

int lowbit(int x)
{
    return x&(-x);
}

int main()
{
    int n;
    cin>>n;
    while(n--)
    {
        int x;
        cin>>x;
        int res=0;
        int cnt=0;
        while(x)
        {
            x-=lowbit(x);
            res++;
        }
        while (x) {
            x = x & (x - 1);
            cnt++;
        }
        cout<<res<<" "<<cnt<<endl;
    }
}
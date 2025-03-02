#include<iostream>
#include<algorithm>
using namespace std;

const int N=1010;
int a[N];
int s[N];
int res;

int main()
{
    int n;
    cin>>n;
    for(int i=1;i<=n;i++)
    {
        cin>>a[i];
    }
    for(int i=1,j=1;i<=n;i++)
    {
        s[a[i]]++;
        while(s[a[i]>1])
        {
            s[a[j++]]--;
        }
        res=max(res,i-j+1);
    }
}
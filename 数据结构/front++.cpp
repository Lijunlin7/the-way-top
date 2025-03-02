#include<iostream>
using namespace std;
const int N=1010;
int main()
{
    int q[N];
    int s[N];
    int n,m;
    cin>>n>>m;
    for(int i=1;i<=n;i++)
    {
        cin>>q[i];
        s[i]=s[i-1]+q[i];
    }
    while(m--)
    {
        int l,r;
        cin>>l>>r;
        cout<<s[r]-s[l-1];
    }
}
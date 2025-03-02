#include<iostream>
using namespace std;

const int N=1010;
int n,m;
int a[N],b[N];
void insert(int l,int r,int c)
{
    b[l]+=c;
    b[r+1]-=c;
}
int main()
{
    cin>>n>>m;
    for(int i=1;i<=n;i++)
    {
        cin>>a[i];
        insert(i,i,a[i]);
    }
    while(m--)
    {
        int l,r,c;
        cin>>l>>r>>c;
        insert(l,r,c);
    }
    for(int i=1;i<=n;i++)
    {
        a[i]=a[i-1]+b[i];
        cout<<a[i];
    }
}
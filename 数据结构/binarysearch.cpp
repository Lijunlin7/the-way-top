#include<iostream>
using namespace std;

const int N=1010;

int lowerbinary_search(int q[],int l,int r,int x)
{
    while(l<r)
    {
        int mid=(l+r)/2;
        if(q[mid]>=x)
        {
            r=mid;
        }
        else
        {
            l=mid+1;
        }
    }
    return l;
}
int upper(int q[],int l,int r,int x)
{
    while(l<r)
    {
        int mid=(l+r)/2;
        if(q[mid]>x)
        {
            r=mid;
        }
        else
        {
            l=mid+1;
        }
    }
    return l;
}
int main()
{
    int a[N]={0};
    int n=0;
    cin>>n;
    for(int i=0;i<n;i++)
    {
        cin>>a[i];
    }
    int m=0;
    cin>>m;
    cout<<lowerbinary_search(a,0,n-1,m)<<endl;
}
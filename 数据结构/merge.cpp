#include<iostream>
using namespace std;

const int N=1010;
int t[N];

void merge(int a[],int l,int r)
{
    if(l>=r)return;
    int mid=(l+r)/2;
    merge(a,l,mid);
    merge(a,mid+1,r);
    int i=l,j=mid+1,k=0;
    while(i<=mid&&j<=r)
    {
        if(a[i]<=a[j])
        {
            t[k++]=a[i++];
        }
        else
        {
            t[k++]=a[j++];
        }
    } 
    while(i<=mid)
    {
        t[k++]=a[i++];
    }
    while(j<=r)
    {
        t[k++]=a[j++];
    }
    for(i=l,j=0;i<=r;i++,j++)
    {
        a[i]=t[j];
    }
}
int main()
{
    int n;
    cin>>n;
    int a[N]={0};
    for(int i=0;i<n;i++)
    {
        cin>>a[i];
    }
    merge(a,0,n-1);
    for(int i=0;i<n;i++)
    {
        cout<<a[i];

    }
}
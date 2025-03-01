#include<iostream>
using namespace std;

const int N=1010;

void quick(int a[],int l, int r,int k)
{
    if(l>=r)return;
    int i=l-1,j=r+1,mid=a[(l+r)/2];
    while(i<j)
    {
        do
        {
            i++;
        }while(a[i]<mid);
        do
        {
            j--;
        } while (a[i]>mid);
        if(i<j)
        {
            int temp=a[i];
            a[i]=a[j];
            a[j]=temp;
        }
    }
    int len=j-l+1;
    if(k<=len)
    {
        quick(a,l,j,k);
    }
    else
    {
        quick(a,j+1,r,k-len);
    }
}
int main()
{
    int n;
    cin>>n;
    int  a[N]={0};
    for(int i=0;i<n;i++)
    {
        cin>>a[i];
    }
    
}
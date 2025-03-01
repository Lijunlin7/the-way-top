#include<iostream>
using namespace std;

const int N=1010;
void fast_sort(int a[],int l,int r)
{
    if(l>=r) return;
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
        } while (a[j]>mid);
        if(i<j)
        {
        int temp=a[i];
        a[i]=a[j];
        a[j]=temp;  
        }

    }
    fast_sort(a,l,j);
    fast_sort(a,j+1,r);
}
int main()
{
    int n;
    int a[N]={0};
    cin>>n;
    for(int i=0;i<n;i++)
    {
        cin>>a[i];
    }
    fast_sort(a,0,n-1);
    for(int i=0;i<n;i++)
    {
        cout<<a[i]<<" ";
    }
    cout<<endl;

}
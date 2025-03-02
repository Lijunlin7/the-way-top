#include<iostream>
using namespace std;

const int N = 1e5 + 10;
int a[N];
int b[N];

int main()
{
    ios::sync_with_stdio(false);
    int n,m;
    cin>>n>>m;
    for(int i=0;i<n;i++)cin>>a[i];
    for(int i=0;i<m;i++)cin>>b[i];
    int i=0;
    for(int j=0;j<m;j++)
    {
        if(i<n&&a[i]==b[j])
        {
            i++;
        }
    }
    if(i==n)
    {
        cout<<"yes"<<endl;
    }
    else{
        cout<<"no"<<endl;
    }
}
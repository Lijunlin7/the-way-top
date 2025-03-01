#include<iostream>
#include<algorithm>
using namespace std;

const int N=1010;
int n,m;
int w[N],v[N];
int res;
void dfs(int u,int r,int s)
{
     if(u==n+1)
     {
        res=max(res,s);
        return;
     }
     if(v[u]<=r)
     {
        dfs(u+1,r-v[u],s+w[u]);
     }
     dfs(u+1,r,s);
} 

int main()
{
    cin>>n>>m;
    for(int i=1;i<=n;i++)
    {
        cin>>v[i]>>w[i];
    }
    dfs(1,m,0);
    printf("%d\n",res);
    return 0;
}
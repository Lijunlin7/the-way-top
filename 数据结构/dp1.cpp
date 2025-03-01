#include <iostream>
#include<algorithm>

using namespace std;
const int N = 1010;
int n, m;
int w[N], v[N];
int f[N][N];

int dfs(int u,int r)
{
    if(~f[u][r])
    {
        return f[u][r];
    }
    if(u==n+1)return 0;
    if(v[u]<=r)
    {
        return f[u][r]=max(dfs(u+1,r),dfs(u+1,r-v[u])+w[u]);

    }
    return f[u][r]=dfs(u+1,r);
}
int main()
{
    memset(f,-1,sizeof f);
    cin>>n>>m;
    for(int i=1;i<=n;i++)
    {
        cin>>v[i]>>w[i];
    }
    printf("%d\n",dfs(1,m));
    return 0;
}
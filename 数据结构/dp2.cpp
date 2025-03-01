#include<iostream>
#include<algorithm>
using namespace std;

const int N = 1010;
int n, m;
int f[N][N];

int main()
{
    cin>>n>>m;
    for(int i=1;i<=n;i++)
    {
        int v,w;
        cin>>v>>w;
        for(int j=1;j<=m;j++)
        {
            f[i][j]=f[i-1][j];
            if(j>=v)
            f[i][j]=max(f[i][j],f[i-1][j-v]+w);
        }
    }
    printf("%d\n",f[n][m]);
    return 0;
}

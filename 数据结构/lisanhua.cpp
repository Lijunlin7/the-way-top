#include <iostream>
#include<utility>
#include<algorithm>

using namespace std;
typedef pair<int, int> PII;

const int N = 300010;
int a[N], al;
int b[N], s[N]; // 假定有一个无限长的数轴，数轴上每个坐标上的数都是 0

PII q[N], p[N];
int ql, pl;

int n, m;

// 手写二分
int lower_bound(int q[], int l, int r, int x) {
    while (l < r) {
        int mid = (l + r) >> 1;
        if (q[mid] >= x)
            r = mid;
        else
            l = mid + 1;
    }
    return l;
}

int main() {
    cin >> n >> m;
    while (n--) {
        int x, c;
        cin >> x >> c;
        p[pl++] = {x, c};
        a[al++] = x;
    }

    int l, r;
    while (m--) {
        cin >> l >> r;
        q[ql++] = {l, r};
        a[al++] = l, a[al++] = r;
    }

    // ① 排序+去重
    sort(a, a + al);
    // ② 使用STL的去重函数去重，不用手写的去重，原因：只排序一次，去重一次，不像是二分需要重复使用，性能差别不大，但代码就短的多
    al = unique(a, a + al) - a;

    // 处理一下某个x上加c的事情
    for (int i = 0; i < pl; i++) {
        int x = lower_bound(a, 0, al, p[i].first) + 1; // 下标从0开始，需要加1个偏移量
        b[x] += p[i].second;
    }

    // 一维前缀和
    for (int i = 1; i < N; i++) s[i] = s[i - 1] + b[i];

    // 处理询问(前缀和应用)
    for (int i = 0; i < ql; i++) {
        // 根据原来的位置值，计算出映射后的位置值
        l = lower_bound(a, 0, al, q[i].first) + 1;
        r = lower_bound(a, 0, al, q[i].second) + 1;
        // 利用一维前缀和计算区间和
        printf("%d\n", s[r] - s[l - 1]);
    }
    return 0;
}

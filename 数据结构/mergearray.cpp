#include<iostream>
#include<utility>
#include<vector>
#include<algorithm>
using namespace std;
typedef pair<int, int> PII;
vector<PII> segs;
vector<PII> res;

void merge()
{
    if(!segs.size())return;
    sort(segs.begin(),segs.end());
    int st=segs[0].first,ed=segs[0].second;
    for(int i=1;i<segs.size();i++)
    {
        if(ed<segs[i].first)
        {
            res.push_back({st,ed});
            st=segs[i].first,ed=segs[i].second;

        }
        else
        {
            ed=max(ed,segs[i].second);
        }
        res.push_back({st,ed});
    }
}
    int main() 
    {
        int n;
        cin >> n;
        while (n--) {
            int l, r;
            cin >> l >> r;
            segs.push_back({l, r});
        }
        merge();
        printf("%d\n", res.size());
        return 0;
    }


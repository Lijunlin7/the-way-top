#include<iostream>
#include<string>
using namespace std;
class Person
{
    public:
    Person(string Name,int Age):name(Name),age(Age)
    {

    }
    void greet()
    {
        cout<<"Hello,I am "<<name<<",and I am "<<age<<" years old"<<endl;
    }
    
    private:
    string name;
    int age;

};
int main()
{
    Person a("Tom",18);
    a.greet();
}
补充: 类C语言有关操作补充
1.元素类型说明

顺序表类型定义
```
typedef struct {
    ElemType data[]; // 顺序表中元素的类型
    int length;   
} SqList; // 顺序表类型

// 定义类型
typedef char ElemType;
typedegf int Elemtype;

// 结构类型
typedef struct {
    float p;
    int e;
} Polynomial;

typedef struct {
    Polynomial *elem;
    int length;
} SqList;
```

数组定义
```
// 数组静态分配
typedef struct {
    ElemType data[MaxSize]; // data存放的是data[0]的元素地址,基地址
    int length;
} SqList; // 顺序表类型

// 数组动态分配
typedef struct {
    ElemType *data; // 指针变量
    int length;
} SqList; // 顺序表类型
```

C语言的内存动态分配
```
SqList L;
L.data=(ElemType*)malloc(sizeof(ElemType)*MaxSize); // 内存分配函数
L.data得到元素的基地址

sizeof(ElemType)*MaxSize
ElemType传入的类型, sizeof会计算出该类型所占空间大小, 比如char一个字节, int四个字节

(ElemType*) 强制内存转换运算, 指向元素类型的指针
sizeof计算出内存大小后给malloc函数, 这时候malloc函数需要知道这一空间需要分配给什么类型
如果ElemType占八个字节, 进行内存划分, 那么就要把这一内存空间划分成100个小空间
```
1.malloc(m)函数, m为整数, 开辟m字节长度的地址空间, 并返回这段空间的首地址
2.sizeof(x)运算, x可以是类型或者变量, 计算变量x的长度
3.free(p)函数, 释放指针p所指变量的存储空间, 即彻底删除一个变量
需要加载头文件: stdlib.h

C++的动态存储分配
new 类型名T (初值列表)
    功能:
        申请用于存放T类型对象的内存空间, 并依初值列表赋以初值
    结果值:
        成功: T类型的指针, 指向新分配的内存
        失败: 0(NULL)
int *p1 = new int;
或int *p1 = new int(10);
        
delete 指针p
功能:
    释放指针P所指向的内存, P必须是new操作的返回值
delete p1;

C++中的参数传递
1.函数调用时传送给形参表的实参必须与形参三个一直类型、个数、顺序
2.参数传递两种方式
    1.传值方式(参数为整型, 实型, 字符型等) 形参实参不共用内存
    2.传地址 形成实参公用内存
        参数为指针变量 data[MaxSize]
        参数为引用类型
        参数为数组名 *data
        
传值方式
把实参的值传送给函数局部工作区相应的副本中, 函数使用这个副本执行必要的功能.
函数修改的是副本的值, 实参的值不变
```
#include <iostream.h>
void swap(float m, float n) {
    float temp;
    temp = m;
    m = n;
    n = temp;
}

void main() {
    float a,b;
    cin >> a >> b;
    swap(a, b);
    count<<a<<end<<b<<endl;
}
```

传地址方式 -> 指针变量作参数
1.形参变化影响实参
传入的是地址, 交换的是内容也就是值
```
#include <iostream.h>
void swap(float *m, float *n) {
    float t;
    t = *m;
    *m = *n;
    *n = t;
}

void main() {
    float a, b, *p1, *p2;
    cin >> a >> b;
    p1 = &a; p2 = &b; // 这里的&表示取地址运算符
    swap(p1, p2);
    count<<a<<end<<b<<endl;
}
```
2.形参变化不影响实参
```
传入的是指针, 交换的也是指针
#include <iostream.h>
void swap(float *m, float *n) {
    float *t;
    t = m;
    m = n;
    n = t;
}

void main() {
    float a, b, *p1, *p2;
    cin >> a >> b;
    p1 = &a; p2 = &b;
    swap(p1, p2);
    count<<a<<end<<b<<endl;
}
```

传地址方式 -> 数组名作参数
1.传递的是数组的首地址
2.对形参数组所做的任何改变都将反映到实参数组中
```
#include<iostream.h>
void sub(char *b) {
    *b = "world";
}
void sub(char b[]) {
    // 对形参数组赋值, 会改变实参数组
    b[] = "world";
}

void main (void) {
    char a[10] = "hello";
    sub(a);
    count<<a<<endl;
}
```

传地址方式 -> 引用类型作参数
什么是引用: 它用来给一个对象提供一个替代的名字
```
// j是一个引用类型, 代表i的一个替代品名i值改变时, j值也跟着改变,
// 所以会输出i=7 j=7, 共用的同一个内存空间
#include<iostream.h>
void main() {
    int i = 5;
    int &j = i;
    i = 7;
    cout<<"i="<<i<<"j="<<j;
}


// 传递的是引用, 交换的是值
#include <iostream.h>
void swap(float& m, float& n) {
    float temp;
    temp = m;
    m = n;
    n = temp;
}

void main() {
    float a,b;
    cin >> a >> b;
    swap(a, b);
    count<<a<<end<<b<<endl;    
}
```

引用类型作形参的三点说明
(1)传递引用给函数与传递指针的效果是一样的, 形参变化实参也发生变化
(2)引用类型作参数, 在内存中并没有产生实参的副本, 它直接对实参操作;
而一般变量作参数, 形参与实参就占用不同的存储单元, 所以形参变量的值
是实参变量的副本, 因此, 当参数传递的数据量较大时, 用引用比用一般变量
传递参数的时间和空间效率都好.
(3)指针参数虽然也能达到与使用引用的效果, 但在被调函数中需要重复使用
"*指针变量名"的形式进行运算, 这很容易产生错误且程序阅读性较差, 另一方面
在主调函数的调用点处, 必须用变量的地址作为实参




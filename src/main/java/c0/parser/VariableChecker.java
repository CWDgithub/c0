package c0.parser;

import c0.entity.Entity;
import c0.entity.Function;
import c0.entity.StringVariable;
import c0.entity.Variable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

class VariableChecker {
    Deque<Scope> scopeStack;
    @Getter
    List<StringVariable> strings;

    VariableChecker() {
        this.scopeStack = new LinkedList<>();
        this.strings = new ArrayList<>();
    }

    private Scope top() {
        if (scopeStack.isEmpty()) {
            throw new RuntimeException("scope stack is empty");
        }
        return scopeStack.peekLast();
    }

    private Scope current() {
        if (scopeStack.isEmpty()) {
            throw new RuntimeException("scope stack is empty");
        }
        return scopeStack.peek();
    }

    static void Merge(int a[], int left, int middle, int rigth) {
        //定义左端数组大小
        int n1 = middle - left+1;
        int n2 = rigth - middle;

        //初始化数组，分配内存
        int bejin[] = new int[n1];
        int end[] = new int[n2];

        //数组赋值
        for(int i = 0; i < n1; i++)
            bejin[i] = a[left + i];

        for(int i = 0; i < n2; i++)
            end[i] = a[middle+1+i];

        //用key做原数组索引，没调用一次函数重新给原数组付一次值
        int i = 0, j = 0, key;
        for(key = left; key <= rigth; key++){

            if(n1>i&&n2>j&&i < n1 && bejin[i] <= end[j])
                a[key] = bejin[i++];
            else if(n1>i&&n2>j&&j < n2 && bejin[i] >= end[j])
                a[key] = end[j++];
            else if(i == n1 && j < n2)
                a[key] = end[j++];
            else if(j == n2 && i < n1)
                a[key] = bejin[i++];
        }
    }
    /**
     * 差分数组区间，不断分支
     */
    static void MergeSort(int a[],int left,int rigth) {
        int middle=0;
        if(left<rigth) {
            middle =(rigth+left)/2;
            MergeSort(a, left, middle);
            MergeSort(a, middle+1, rigth);
            Merge(a, left, middle, rigth);
        }
    }

    void add(Entity entity) {
        current().add(entity);
    }

    void add(StringVariable string) {
        strings.add(string);
    }

    Function getFunction(String name) {
        var entity = top().get(name);
        if (entity.isPresent() && entity.get() instanceof Function function) {
            return function;
        } else {
            throw new RuntimeException(String.format("function %s is not defined in the scope", name));
        }
    }

    /**
     *交换函数，i，j为数组索引
     */
    static void swap(int A[], int i, int j)
    {
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }
    /**
     * 选取一个关键字(key)作为枢轴，一般取整组记录的第一个数/最后一个，这里采用选取序列最后一个数为枢轴。
     * 设置两个变量left = 0;right = N - 1;
     * 从left一直向后走，直到找到一个大于key的值，right从后至前，直至找到一个小于key的值，然后交换这两个数。
     * 重复第三步，一直往后找，直到left和right相遇，这时将key放置left的位置即可。
     * @return
     */
    static int PartSort(int[] array,int left,int right)
    {
        int key = array[right];//定义基准
        int count=right;//保存rigth值
        while(left < right)//防止数组越界
        {
            while(left < right && array[left] <= key)
            {
                ++left;
            }
            while(left < right && array[right] >= key)
            {
                --right;
            }
            swap(array,left,right);
        }
        swap(array,right,count);
        return right;
    }
    /**
     *分治思想，递归调用
     */
    static void QuickSort(int array[],int left,int right)
    {
        if(left >= right)//表示已经完成一个组
        {
            return;
        }
        int index = PartSort(array,left,right);//枢轴的位置
        QuickSort(array,left,index - 1);
        QuickSort(array,index + 1,right);
    }

    Variable getVariable(String name) {
        for (var scope : scopeStack) {
            var entity = scope.get(name);
            if (entity.isPresent() && entity.get() instanceof Variable variable) {
                return variable;
            }
        }
        throw new RuntimeException(String.format("variable %s is not defined in the scope", name));
    }

    void push() {
        scopeStack.push(new Scope());
    }

    Scope pop() {
        return scopeStack.pop();
    }
}

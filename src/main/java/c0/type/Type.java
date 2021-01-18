package c0.type;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Type {
    TypeVal type;

    public Type(TypeVal type) {
        this.type = type;
    }

    public Type(String value) {
        this.type = switch (value) {
            case "int" -> TypeVal.UINT;
            case "void" -> TypeVal.VOID;
            case "double" -> TypeVal.DOUBLE;
            default -> throw new RuntimeException("wrong type");
        };
    }

    public TypeVal getType(){
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TypeVal typeVal) return type == typeVal;
        if (o instanceof Type type2) return type == type2.type;
        return false;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    @Override
    public String toString() {
        String stringBuilder = "Type" +
                "(" + type + ")";
        return stringBuilder;
    }

    /**
     * 简单的程序介绍
     */
    private void outputInformation(){
        System.out.println("欢迎使用C0编译器");
        System.out.println("本编译器实现功能：");
    }

    /**
     * 初始化程序
     *
     */
    private void init(){
        outputInformation();
        ArrayList<String> arrayList= new ArrayList<String>();
        initFile();
    }

    /**
     * 初始化文件
     */
    private void initFile(){
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("input the c0 file name");
            if(readC0File(scanner.next())){
                break;
            }else{
                System.out.println("c0 File read failed. Please try again!");
            }
        }
    }

    /**
     * 读取文件
     * @param fileName 文件名称
     * @return 读取成功返回true，否则返回false
     */
    private boolean readC0File(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        ArrayList<String> arrayList= new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                arrayList.add(tempString);
            }
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    System.out.println("document close failed!");
                }
            }
        }
    }
}

package c0.parser;// package com.imudges.C0Compiler.JavaCC;
/**
 */
public class SymbolItem {
    boolean isConstant;
    boolean isInitialized;
    int stackOffset;

    public SymbolItem(SymbolType type) {
        this.type = type;
        this.name = "";
        this.val = 0;
        this.level = 0;
        this.adr = 0;
        this.size = 0;
        this.returnType = 0;
    }

    public String toString(){
        return type.toString()+"\t"+name+"\t"+adr+"\t"+returnType;
    }

    public enum SymbolType{
        intSym,
        functionSym
    }

    private String name;
    private SymbolType type;
    private int val;
    private int level;
    private int adr;
    private int size;
    private int returnType;

    /**
     * @return the isConstant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    public int getReturnType() {
        return returnType;
    }

    public void setReturnType(int returnType) {
        this.returnType = returnType;
    }

    /**
     * @param isConstant the isConstant to set
     */
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymbolType getType() {
        return type;
    }

    /**
     * @return the stackOffset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }

    public int getAdr() {
        return adr;
    }

    public void setAdr(int adr) {
        this.adr = adr;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

package org.gmock.utils;

public class JavaLoader implements ILoader {

    private String name;

    public JavaLoader(String name){
        this.name = name;
    }

    public String load(String key){
        return "key";
    }

}

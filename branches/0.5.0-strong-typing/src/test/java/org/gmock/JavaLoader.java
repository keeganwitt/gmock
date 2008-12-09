package org.gmock;

public class JavaLoader {

    private String name;

    public JavaLoader(String name){
        this.name = name;
    }

    public String load(String key){
        return "key";
    }

}

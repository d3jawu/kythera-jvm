package me.dejawu.kythera.backend;

import me.dejawu.kythera.runtime.KytheraValue;

// this class is only used for examining its generated assembly with ASMifier
public class Scratch {
    public static void main(String[] args) {
        if(args[0].equals("true")) {
            int a = 0;
        } else {
            int b = 1;
        }
        int c = 3;
    }

/*    public static Integer add(Integer x, Integer y) {
        Integer result = x + y;
        System.out.println(result);
        return result;
    }*/

/*    public static Boolean not(Boolean value) {
        Boolean result = !value;
        System.out.println(result);
        return result;
    }*/
}

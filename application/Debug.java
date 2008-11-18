/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application;

/**
 *
 * @author fred
 */
public class Debug {
    private static boolean printError = false;
    private static boolean printWarning = false;
    private static boolean printMark = false;
    public static void error(String x){
        if(printError){ System.out.println(x); }
    }
    public static void error(boolean b, String x){
        if(b){error(x);}
    }
    public static void warning(String x){
        if(printWarning){ System.out.println(x); }
    }
    public static void warning(boolean b, String x){
        if(b){warning(x);}
    }
    public static void mark(String x){
        if(printMark){ System.out.println(x); }
    }
    public static void showError(){
        printError = true;
    }
    public static void showWarning(){
        printWarning = true;
    }
    public static void showMark(){
        printMark = true;
    }

}

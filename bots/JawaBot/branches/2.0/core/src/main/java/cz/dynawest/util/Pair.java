package cz.dynawest.util;

/**
 *  
 *  @author Ondrej Zizka
 */
public class Pair <A, B> {
   
   public A a;
   public B b;

   public Pair(A a, B b) {
      this.a = a;
      this.b = b;
   }

   public A getA() { return a; }
   public void setA(A a) { this.a = a; }
   public B getB() { return b; }
   public void setB(B b) { this.b = b; }
   
}// class


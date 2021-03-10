package jmetal.myutils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class QSort {

	public  QSort() {
		// TODO Auto-generated constructor stub
	}
	
	public static void quicksort(double n[], int [] index, int left, int right) { 
	        int dp; 
//	        if (right == left) return;
	        if (left < right) { 
	            dp = partition(n,index, left, right); 
	            quicksort(n, index, left, dp - 1); 
	            quicksort(n, index, dp + 1, right); 
	        } 
	    } 

	public static int partition(double n[], int [] index, int left, int right) { 
	        double pivot = n[left]; 
	        while (left < right) { 
	            while (left < right && n[right] >= pivot) 
	                right--; 
	            double temp1 =  n[left] ;
	            n[left] = n[right]; 
	            n[right] = temp1;
	            
	            int temp2 = index[left] ;
	            index[left] = index[right]; 
	            index[right] = temp2;

	          
	            while (left < right && n[left] <= pivot) 
	                left++; 	            

	            temp1 =  n[left] ;
	            n[left] = n[right]; 
	            n[right] = temp1;
	            
	            temp2 = index[left] ;
	            index[left] = index[right]; 
	            index[right] = temp2;

	        } 	        
	        n[left] = pivot; 
	        return left;   
	 }	
	
	
	public static void main(String[] args){
		double []  a =  new double [900];
		int [] index =  new int [900];
		
		Random rand = new Random(); 
		
		for (int k =0; k< 900;k ++) {
			a[k] = rand.nextDouble();
			index[k] = k;
		}
		
		ArrayList <Double>  la =  new ArrayList <Double>();
		ArrayList <Integer>  lindex = new ArrayList <Integer> ();
		
		for (int k =0; k< 900;k ++) {
			a[k] = rand.nextDouble();
			index[k] = k;
			
			la.add(a[k]);
//			 System.out.println(la.get(k)+" ");
			lindex.add(index[k]);
		}
		
		
		long initTime = System.currentTimeMillis();	   // Start timing the algorithm 
//		Arrays.sort(a);
		quicksort(a,index,0,900-1)	;
		long estimatedTime = System.currentTimeMillis() - initTime;	   
		System.out.println("Arrays sort time = "+ estimatedTime + "ms");	 
		       
          for(int i=0;i<a.length;i++){
            System.out.println(a[i]+" ");
//            System.out.print(index[i]+",");
         }		
          System.out.println("\n");
          System.out.println("*******************************");
          
          initTime = System.currentTimeMillis();	   // Start timing the algorithm 
//          quicksort(la,lindex,0,900-1)	;	
          estimatedTime = System.currentTimeMillis() - initTime;	   
          System.out.println("ArrayList sort time = "+ estimatedTime + "ms");	
          for(int i=0;i<a.length;i++){
//              System.out.println(la.get(i)+" ");
//              System.out.print(lindex.get(i)+",");
           }	
          
	}
}

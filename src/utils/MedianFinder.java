package utils;

import java.util.Collections;
import java.util.PriorityQueue;

public class MedianFinder {
    PriorityQueue<Double> maxHeap; //lower half
    PriorityQueue<Double> minHeap; //higher half
 
    public MedianFinder(){
        maxHeap = new PriorityQueue<Double>(Collections.reverseOrder());
        minHeap = new PriorityQueue<Double>();
    }
 
    // Adds a number into the data structure.
    public void addNum(double num) {
        maxHeap.offer(num);
        minHeap.offer(maxHeap.poll());
 
        if(maxHeap.size() < minHeap.size()){
            maxHeap.offer(minHeap.poll());
        }
    }
 
    // Returns the median of current data stream
    public double findMedian() {
        if(maxHeap.size()==minHeap.size()){
        	try{
        		return (double)(maxHeap.peek()+(minHeap.peek()))/2;
        	}catch(Exception e){
        		return 0;
        	}
        }else{
            return maxHeap.peek();
        }
    }
}

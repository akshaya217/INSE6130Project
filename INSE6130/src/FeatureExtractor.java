import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class FeatureExtractor {
	
	private Integer sum(ArrayList<Integer> list) {
	     Integer sum= 0; 
	     for (Integer i:list)
	         sum = sum + i;
	     return sum;
	}
	
	public void extract(float[] times, int[] directions, ArrayList<String> features){
		
		//transmission size features 
		features.add(""+times.length); //how many lines read from the data file
		
		int c = 0;
		for(int i=0; i<directions.length; i++){
			if (directions[i]>0)
				c++;
		}
		
		features.add(""+c);//how many positive sizes
		features.add(""+(times.length - c));//how many negative sizes
		
		features.add(""+(times[-1] - times[0]));//last time - first time
		
		//unique packet lengths
		for(int i=-1500; i<1501; i++){
			if (Arrays.asList(directions).contains(i))
				features.add(""+1);
			else
				features.add(""+0);
		}
		
		//Transposition (similar to good distance scheme)
		c = 0;
		for(int i=0; i<directions.length; i++){
			if(directions[i] > 0){
				c++;
				features.add(""+i);//adding first 300 positive sizes
			}
			if(c == 300)
				break;
		}
		for(int i=c; i<300; i++){
			features.add("X");//if positive sizes are less than 300 pad the rest with "X"
		}
		
		c = 0;
		int previousLocation = 0;
		for(int i=0; i<directions.length;i++){
			if(directions[i] > 0){
				c++;
				features.add(""+(i-previousLocation));//adding distance between each positive size and the previous positive size
				previousLocation = i;
			}
			if(c == 300)//only consider first 300 positive sizes
				break;
		}
		for(int i=c; i<300; i++){
			features.add("X");//if positive sizes are less than 300 pad the rest with "X"
		}
		
		//Packet distributions (where are the outgoing packets concentrated)
		for(int i=0; i< Math.min(directions.length, 3000); i++){
			if(i % 30 != 29){
				if(directions[i]>0)
					c++;
			}else{
				features.add(""+c);//add number of positive sizes in every 29 lines
				c = 0;
			}
		}
		
		for(int i=directions.length/30;i<100;i++){//if the groups of 30 are less than 100 (total < 3000) pad the rest with 0
			features.add(""+0);
		}
		
		//Bursts (no two adjacent incoming packets)
		ArrayList<Integer> bursts = new ArrayList<Integer>();
		int currentBurst = 0;
		int stopped = 0;
		for(int i=0; i<directions.length;i++){
			if(directions[i] < 0){//outgoing
				stopped = 0;
				currentBurst -= directions[i]; 
			}else if(directions[i] > 0 && stopped == 0){//first incoming
				stopped = 1;
			}else if(directions[i] > 0 && stopped == 1){//second incoming
				stopped = 0;
				bursts.add(currentBurst);
			}
		}
		features.add(""+Collections.max(bursts));
		features.add(""+(sum(bursts)/bursts.size()));
		features.add(""+bursts.size());
		
		int[] counts = new int[3];
		counts[0] = 0;
		counts[1] = 0;
		counts[2] = 0;
		for(int i=0; i<bursts.size();i++){
			if(bursts.get(i)>5)
				counts[0]++;
			if(bursts.get(i)>10)
				counts[1]++;
			if(bursts.get(i)>15)
				counts[2]++;
		}
		
		for(int i=0; i<counts.length;i++)
			features.add(""+counts[i]);
		
		for(int i=0; i<5; i++){//add first 5 bursts
			//if(bursts[i] != "X")
			features.add(""+bursts.get(i));
		}
		
		for(int i=0; i<20; i++){//add first 20 sizes +1500
			//if(bursts[i] != "X")
			features.add(""+(directions[i]+1500));
		}
		
	}//end of extract method

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

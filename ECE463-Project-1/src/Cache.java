import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Cache {
	
	public static final int hit = 1;
	public static final int miss = 0;
	
	public static final int validIndex = 0;
	public static final int dirtyIndex = 1;
	public static final int lruIndex = 2;
	public static final int tagIndex = 3;
	public static final int read = 0;
	public static final int write = 1;
	
	public static int counter = 0;
	
	int blockSize;
	int lSize;
	int lAssoc;
	int vcNumBlocks;
	int writeCount = 0; 
	int readCount = 0;
	int writeMissCount = 0;
	int readMissCount = 0;
	int indexNum;
	int offSetNum;
	int indexMask;
	int tagMask;
	int writeBackCount = 0;
	public static int totalMemTraff = 0;
	
	Cache writeTo;
	
	HashMap<Integer, int[][]> cache;

	public Cache(int blockSize, int lSize, int lAssoc, int vcNumBlocks, Cache writeTo) throws IOException {
		this.blockSize = blockSize;
		this.lSize = lSize;
		this.lAssoc = lAssoc;
		this.vcNumBlocks = 0;
		this.writeTo = writeTo;
		//this.l2Size = l2Size;
		//this.l2Assoc = l2Assoc;
		
		
		
		//System.out.println(this.lSize/(this.lAssoc*this.blockSize));
		this.cache = new HashMap<Integer, int[][]>();
		
		this.offSetNum = (int) (Math.log(this.blockSize)/Math.log(2));
		this.indexNum = (int) (Math.log(this.lSize/(this.lAssoc*this.blockSize))/Math.log(2));
		
		//System.out.println(offSetNum);
		//System.out.println(indexNum);
		
		this.indexMask = ((int) Math.pow(2, this.indexNum) - 1);
		//System.out.println("index mask is " + indexMask);
		this.tagMask = ((int) Math.pow(2, 32-(this.indexNum + this.offSetNum)) -1);
		//System.out.println("tag mask is " + tagMask);
		//System.out.println("tag mask: " + tagMask);
		//int numSetsL1 = l1Size/(l1Assoc*blockSize);
		//int numSetsL2 = l2Size/(l2Assoc*blockSize);
		
	}
	
	public int write(int index, int tag)
	{
		return 0;
	}
	
	public int readReq(int index, int tag)
	{
		return 0;
	}
	
	public int writeBack(int index, int tag)
	{
		return 0;
	}
	
	public int allocate(int index, int tag)
	{	
		return 0;
	}
	
	public int read(int index, int tag) {
		return 0;
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		int blockSize = Integer.parseInt(args[0]);
		int l1Size = Integer.parseInt(args[1]);
		int l1Assoc = Integer.parseInt(args[2]);
		int vcNumBlocks = Integer.parseInt(args[3]);
		int l2Size = Integer.parseInt(args[4]);
		int l2Assoc = Integer.parseInt(args[5]);
		int rW = read;
		String traceFile = args[6];
		
		BufferedReader br = new BufferedReader(new FileReader(traceFile));
		
		System.out.println("===== Simulator configuration =====");
		System.out.println("  BLOCKSIZE:                  " + blockSize);
		System.out.println("  L1_SIZE:                  " + l1Size);
		System.out.println("  L1_ASSOC:                    " + l1Assoc);
		System.out.println("  VC_NUM_BLOCKS:               0");
		System.out.println("  L2_SIZE:                     " + l2Size);
		System.out.println("  L2_ASSOC:                    " + l2Assoc);
		System.out.println("  trace_file:      " + traceFile);
		System.out.println();
		System.out.println("===== L1 contents =====");
		
		try {
			//Cache c = new Cache(blockSize, l1Size, l1Assoc, vcNumBlocks, l2Size, l2Assoc, null);
			Cache c2 = null;
			
			if (l2Size > 0) {
				c2 = new Cache(blockSize, l2Size, l2Assoc, vcNumBlocks, null);
			}
			Cache c1 = new Cache(blockSize, l1Size, l1Assoc, vcNumBlocks, c2);
		 
		
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				
				String hex = line.substring(2);
				int fullAddr = (int) Long.parseLong(hex, 16);
				String action = line.substring(0, 1);
				
				
				if (action.equals("r"))
				{
					rW = read;
				}
				else
				{
					rW = write;
				}
				
				c1.processInput(fullAddr, rW);	
			}
			
			int[][] tmpSet = new int[c1.lAssoc][4];
			
			//for(Map.Entry<Integer, int[][]> entry : c1.cache.entrySet())
			for (int j = 0; j < c1.cache.size(); j++)
			{
				//System.out.println("set " + Integer.toHexString(entry.getKey()) + ":");
				//System.out.print("set " + entry.getKey() + ":  ");
				System.out.print("set " + (j) + ":  ");
				//int[][] val = entry.getValue();
				int[][] val = c1.cache.get(j);
				for (int i = 0; i < val.length; i++)
				{
					tmpSet[val[i][lruIndex]] = val[i];
					//System.out.println("tag is: " + Integer.toHexString(val[i][tagIndex]));
				}
				for (int i = 0; i < val.length; i++)
				{
					//System.out.
					System.out.print(Integer.toHexString(tmpSet[i][tagIndex]) + " ");
					if (tmpSet[i][dirtyIndex] == 1)
					{
						System.out.print("D ");
					}
				}
				System.out.println();
			}
			
			
			if (c2 != null)
			{
				System.out.println();
				System.out.println("===== L2 contents =====");
				tmpSet = new int[c2.lAssoc][4];
			for (int j = 0; j < c2.cache.size(); j++)
			{
				//System.out.println("set " + Integer.toHexString(entry.getKey()) + ":");
				//System.out.print("set " + entry.getKey() + ":  ");
				System.out.print("set " + (j) + ":  ");
				//int[][] val = entry.getValue();
				int[][] val = c2.cache.get(j);
				for (int i = 0; i < val.length; i++)
				{
					tmpSet[val[i][lruIndex]] = val[i];
					//System.out.println("tag is: " + Integer.toHexString(val[i][tagIndex]));
				}
				for (int i = 0; i < val.length; i++)
				{
					//System.out.
					System.out.print(Integer.toHexString(tmpSet[i][tagIndex]) + " ");
					if (tmpSet[i][dirtyIndex] == 1)
					{
						System.out.print("D ");
					}
				}
				System.out.println();
			}
			}
			
			System.out.println("");
			System.out.println("===== Simulation results =====");
			
			System.out.println("a. number of L1 reads: "+c1.readCount);
			System.out.println("b. number of L1 read misses: "+c1.readMissCount);
			System.out.println("c. number of L1 writes: "+c1.writeCount);
			System.out.println("d. number of L1 write misses: "+c1.writeMissCount);
			
			System.out.println("e. number of swap requests: 0");
			System.out.println("f. swap request rate: 0.0000");
			System.out.println("g. number of swaps: 0");
			
			float missRate = (float) (c1.readMissCount+c1.writeMissCount)/(c1.readCount+c1.writeCount);
			System.out.printf("h. combined L1+VC miss rate: %.4f\n", missRate );
			//System.out.println("h. combined L1+VC miss rate: ");
			System.out.println("i. number writebacks from L1/VC: "+c1.writeBackCount);
			
			if (c2 != null)
			{
			System.out.println("j. number of L2 reads: "+c2.readCount);
			System.out.println("k. number of L2 read misses: "+c2.readMissCount);
			System.out.println("l. number of L2 writes: "+c2.writeCount);
			System.out.println("m. number of L2 write misses: "+c2.writeMissCount);
			missRate = (float) (c2.readMissCount)/(c2.readCount);
			System.out.printf("n. L2 miss rate: %.4f\n", missRate );
			System.out.println("o. number of writebacks from L2: " + c2.writeBackCount);
			}
			else
			{
				System.out.println("j. number of L2 reads: 0");
				System.out.println("k. number of L2 read misses: 0");
				System.out.println("l. number of L2 writes: 0");
				System.out.println("m. number of L2 write misses: 0");
				System.out.printf("n. L2 miss rate: 0.0000\n");
				System.out.println("o. number of writebacks from L2: 0");
			}
			
			
			System.out.println("p. total memory traffic: " + totalMemTraff);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void processInput(int fullAddr, int readWrite){
		
				counter++;
				
				int hitMiss = miss;
				//int readWrite = read;
				int oldCount;
				
				int index = fullAddr >> this.offSetNum;
				index = index & this.indexMask;
				//System.out.println("The index: " + Integer.toHexString(index));
				
				int tag = fullAddr >> (this.indexNum + this.offSetNum);
				tag = tag & this.tagMask;
				//System.out.println("The tag: " + Integer.toHexString(tag));
				
				if (readWrite == read)
				{
					//System.out.println("read");
					this.readCount++;
					readWrite = read;
					//action = read;
					//this.read(index, tag);
				}
				else
				{
					//System.out.println("write");
					this.writeCount++;
					readWrite = write;
					//action = write;
					//this.write(index, tag);
				}
				
				//Check cache to see if item is in
				int[][] set = this.cache.get(index);
				
				//System.out.println(set);
				
				if (set != null)
				{
					//There was a matching set for the index
					oldCount = set.length-1;
					for (int i =0; i < set.length; i++)
					{
						if ((set[i][validIndex] == 1) && (set[i][tagIndex] == tag))
						{
							//Tag was found
							if (readWrite == write) set[i][dirtyIndex] = 1;
							oldCount = set[i][lruIndex];
							set[i][lruIndex] = -1;
							//System.out.println("hit");
							hitMiss = hit;
							break;
						}
					}
				}
				else
				{
					//This means nothing was found for index
					//Crate an empty set for the index
					set = new int[lAssoc][4];
					oldCount = set.length-1;
				}
				
				
				int lru = set.length - 1;
				int victim = 0;
				
				//Obtain the victim
				if (hitMiss == miss)
				{
					if (this.writeTo == null) totalMemTraff++;
					//System.out.println("miss");
					for (int i=0; i < set.length; i++)
					{
						//System.out.println("valid index: " + set[i][validIndex]);
						if (set[i][validIndex] == 0)
						{
							victim = i;
							break;
						}
						else if (set[i][lruIndex] == lru)
						{
							//System.out.println("found max lru: " + set[i][lruIndex]);
							victim = i;
							break;
						}
					}
					
					//read from lower cache
					//if (this.writeTo != null)
					//{
						//writebacks
					if(set[victim][dirtyIndex] == 1)
					{
						int reAss = ((set[victim][tagIndex] << (indexNum)) | index) << offSetNum;
						if (this.writeTo != null) this.writeTo.processInput(reAss, write);
						this.writeBackCount++;
						if (this.writeTo == null) totalMemTraff++;
					}
						
						//read from lower cache
					if (this.writeTo != null) this.writeTo.processInput(fullAddr, read);
					//}
					
					//allocate
					set[victim][validIndex] = 1;
					if (readWrite == read)
					{
						set[victim][dirtyIndex] = 0;
					}
					else
					{
						set[victim][dirtyIndex] = 1;
					}
					
					set[victim][tagIndex] = tag;
					set[victim][lruIndex] = -1;
					//System.out.println("putting: " + index + ": " + tag);
					this.cache.put(index, set);
					
					if (readWrite == write)
					{
						this.writeMissCount++;
						this.readReq(index, tag);
					}
					else this.readMissCount++;
				}
				
				//update lrus whether hit or miss
				for (int i=0; i < set.length; i++)
				{
					if ((set[i][validIndex] == 1) && (set[i][lruIndex] < oldCount))
					{
						set[i][lruIndex]++;
					}
				}
	}
	
}
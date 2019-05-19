import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.*;

public class apriori {
   public static void main(String[] args) throws IOException{
	   
	  File output = new File("output.txt");//File for output

	  ArrayList<String> attributes = new ArrayList<String>();
	  Scanner input = new Scanner(System.in);
	  Map<Set<String>, Integer> map = new HashMap<Set<String>,Integer>();//contain candidate items
	  Set<Set<String>> table = new HashSet<Set<String>>();//contain all rows of the table
	  String fileName;
	  double confidence, support;
	  
	  //Prompt the user to input file name, confidence and support
      System.out.print("Enter file name: ");
      fileName = input.nextLine();
      System.out.print("Enter confidence: ");
      confidence = input.nextDouble();
      System.out.print("Enter support: ");
      support = input.nextDouble();
      
      int n = 0;//total elements in the table
      File file = new File(fileName);
	  Scanner inputFile = new Scanner(file);
	  String line;
	  Integer i = 0;
	  line = inputFile.nextLine();
	  Set<String> lineSet = new HashSet<String>();
	  StringTokenizer token = new StringTokenizer(line, " ");
	  
	  //Save the attribute column
	  while(token.hasMoreTokens())
		  attributes.add(token.nextToken());
	  
	  //save the first item set inside HashMap map
	  while(inputFile.hasNext())
	  {
	
		  table.add(lineSet);
		  int count = 0;
		  line = inputFile.nextLine();
		  token = new StringTokenizer(line, " ");
		 
		  lineSet = new HashSet<String>();
		  while(token.hasMoreTokens())
		  { 
			  n++;
			  Set set = new HashSet<String>();
			  String currentToken = attributes.get(count) + " = " + token.nextToken();
			  lineSet.add(currentToken);
			  set.add(currentToken);
			  if(map.containsKey(set))
				  i = map.get(set)+1;
			  map.put(set,i);
			  count++;
		  }
	  }
	  deletNonFrequent(map, support, confidence, table, output);
	  
	  int previousMapSize = map.size();//The size of the previous set size
	  ArrayList<Set<String>> tempList = new ArrayList<Set<String>>();
	  generateItemSet(map, tempList);
	  updateHashMap(map,tempList, table, confidence, support, output);
	  
	  previousMapSize = map.size();
	  generateItemSet(map, tempList);
	  updateHashMap(map,tempList, table, confidence, support, output);
	  
	  //keep running until there is no new frequent items generated
	  while(map.size() != previousMapSize)
	  {
		  previousMapSize = map.size();
		  generateItemSet(map, tempList);
		  updateHashMap(map,tempList, table, confidence, support, output);
	  }
	  
	  System.out.println("Thank you, the results are written to output.txt");

   }

   //Delete none frequent items from the hashMap and generate items
   public static void deletNonFrequent(Map<Set<String>,Integer> map, double support, double confidence, Set<Set<String>> table, File file) throws IOException
   {
	   	  
	   	  FileWriter writter = new FileWriter(file);
		  writter.write("Summary:" + System.getProperty("line.separator") + "Total rows in the original set: " + table.size());
		  writter.write(System.getProperty("line.separator") + "The selected measure: Confidence: " + confidence + ", Support: " + support);
		  writter.write(System.getProperty("line.separator") + "-------------------------------"+ System.getProperty("line.separator") + "Discovered Rules:" + System.getProperty("line.separator"));
	   	  int count = 0;
		  ArrayList<Set<String>> deleteSets = new ArrayList<Set<String>>();
		  
		  
		  for (Map.Entry<Set<String>, Integer> entry : map.entrySet())
		  {
			  ArrayList<String> rules = new ArrayList<String>();
			  double supportRate = (double) entry.getValue()/(double) table.size();
			  double confidenceRate;
			  Iterator iterate = entry.getKey().iterator();
			  boolean found = true;
			  while(iterate.hasNext())
			  {
				  //get one item from the current set
				  String currentToken =(String) iterate.next();
				  Set<String> currentSet = new HashSet<String>();
				  currentSet.add(currentToken);
				  //get all other items in the current set
				  Set<String> remaining = new HashSet<String>();
				  Iterator iterate2 = entry.getKey().iterator();
				  while(iterate2.hasNext())
				  {
					  String secondToken = (String) iterate2.next();
					  if(secondToken != currentToken)
						  remaining.add(secondToken);
				  }
				  //test different combination of to discover new rules
				  if(remaining.size()!=0 && currentSet.size()!=0 && !rules.contains(remaining + "--->" + currentSet))
				  {
					  if((supportRate < support) || ((double)(entry.getValue()/(double)map.get(currentSet)) < confidence)) 
						  found = false;
					  else
					  {
						  if(!rules.contains(currentSet + "--->" + remaining));
						  {	
							  count++;
							  writter.write(System.getProperty("line.separator") + "Rule#"+ count + " "+ "(Confidence: "+((double)(entry.getValue()/(double)map.get(currentSet)))+" , Support: " + supportRate + ")" +System.getProperty("line.separator") +currentSet + "--->" + remaining);
							  rules.add(currentSet + "--->" + remaining);
						  }
					  }
				  }
				  //test different combination of to discover new rules
				  if(remaining.size()!=0 && currentSet.size()!=0 && !rules.contains(remaining + "--->" + currentSet))
				  {
					  if((supportRate < support) || ((double)(entry.getValue()/(double)map.get(remaining)) < confidence))
						  found = false;
					  else
					  {
						  if(!rules.contains(remaining + "--->" + currentSet))
						  {
							  	count++;
						  		writter.write(System.getProperty("line.separator") + "Rule#"+ count + " "+ "(Confidence: "+((double)(entry.getValue()/(double)map.get(remaining)))+" , Support: " + supportRate +")" +System.getProperty("line.separator")+remaining + "--->" + currentSet);					  
						  		rules.add(remaining + "--->" + currentSet);
						  }
					  }
				  }
			  }
			  if(found == false)
				  deleteSets.add(entry.getKey());
		  }	
		  
		  //delete none frequent items
		  while(deleteSets.size()>0)
		  {
			  map.remove(deleteSets.get(0));
			  deleteSets.remove(0);
		  }
		  writter.close();
   }

   public static void generateItemSet(Map<Set<String>,Integer> map, ArrayList<Set<String>> tempList)
   {
		  //add the value of the HashMap to a Set<Set<String>
		  Set<Set<String>> setList = new HashSet<Set<String>>();
		  for (Map.Entry<Set<String>, Integer> entry : map.entrySet())
		  {
			  setList.add(entry.getKey());
		  }

		  //Join the current item set to generate new combinations
		  Iterator iterList1 = setList.iterator();
		  while(iterList1.hasNext())
		  {
			  Set<String> firstListItem = (Set<String>) iterList1.next();
			  Iterator iterList2 = setList.iterator();
			  //perform join operation
			  while(iterList2.hasNext())
			  {  
				  Set<String> tempSet = new HashSet<String>();
				  tempSet.addAll((Set<String>) iterList2.next());
				  tempSet.addAll(firstListItem);
				  tempList.add(tempSet);
			  }
		  }
   }
   //check if the any of the generated combinations are contained in the table and add them to the hashMap
   public static void updateHashMap(Map<Set<String>,Integer> map, ArrayList<Set<String>> tempList, Set<Set<String>> table, double confidence, double support, File file) throws IOException
   {
	   for(int j=0; j<tempList.size(); j++)
	   {
			  int count =0;
			  Set<String> currentSet = tempList.get(j);

			  Iterator iterTable = table.iterator();
			  //check if the generated combinations are contains the table
			  while(iterTable.hasNext())
			  {
				  boolean found = true;
				  Iterator iterList3 = currentSet.iterator();
				  Set<String> tableRow = (Set<String>) iterTable.next();
				  //check if each element of the set exist in the current row of the time
				  while(iterList3.hasNext())
				  {
					  if(!tableRow.contains((iterList3.next())))
						  found = false;	  
				  }
				  //if all elements exist then increment the count
				  if(found == true)
					  count++;
			  }
			  //add the values to the map and update the count if it does not already exist
			  if(!map.containsKey(currentSet))
				  map.put(currentSet,count);
	   }
	   //remove none frequent items
	   deletNonFrequent(map, support, confidence, table, file);
   	}
}
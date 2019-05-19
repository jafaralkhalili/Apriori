This program is an implementation of the association rule mining
algorithm Apriori in Java
   
- Overview the program code:

 *The entire program including main() were written in one source file.
 *Results (Rules)are written into a file “output” using filewriter.
  Apriori.java - the main  part of this program. 
 
  program structure:
  
  main()—> read data, generate first itemsets.
      
	deleteNonFrequent()—> called by main to prune non satisfactory item sets from first 			              generated item sets	
		calcaulate sup, con and compare it against specified min_supp, min_conf
		then prune unsatisfactory itemsets.
                then writes the satisfactory rules into output.txt.
       
	generateItemSet(…) -> called by main to generate the 2-itemsets.
        
        updateHashMap(…) ->checks frequencies of a generated itemset and send infrequent item sets
	to delete function.

	while(…) -> boolean checks Hashmap size if equiv. to previous Hashmap size, 
                    hence, no more items generated —> no more satisfactory sets could be             
                    generated—>stop
            
 	    deleteNonFrequent(…): called by loop to check new itemsets
            generateItemSet(…): called  by loop  to generate k-itemsets

        
       
  

Run the program:
   Compile javac Apriori.java.     
   Run java Apriori
                      

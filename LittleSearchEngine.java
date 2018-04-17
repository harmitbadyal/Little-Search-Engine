package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	private ArrayList<String> allKeyWords = new ArrayList<String>();
	
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	
	private void addToKeyWordArray(String word)
	{
		boolean shouldAdd = true;
		for(int x=0;x<allKeyWords.size();x++)
		{
			if(allKeyWords.get(x).equals(word))
			{
				shouldAdd = false;
				break;
			}
		}
		if(shouldAdd)
		{
			allKeyWords.add(word);
		}
	}
	
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		if(docFile==null)
		{
			throw new FileNotFoundException();
		}
		int repeats = 0;
		int noiseWords =0;
		HashMap<String,Occurrence> map = new HashMap<String,Occurrence>(1000,2.0f);
		Scanner scanner = new Scanner(new File(docFile));
		while(true)
		{
			if(scanner.hasNext()==false)
			{
				break;
			}
			String word = scanner.next();
			word = getKeyword(word);
			if(word!=null&&word!="")
			{
				if(map.containsKey(word))
				{
					Occurrence occurrence = map.get(word);
					occurrence.frequency = occurrence.frequency+1;
					map.replace(word,occurrence);
					repeats++;
				}
				else
				{
					addToKeyWordArray(word);
					Occurrence occurrence = new Occurrence(docFile,1);
					map.put(word, occurrence);
				}
			}
			else
			{
				noiseWords++;
			}
		}
		return map;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for(int x =0;x<allKeyWords.size();x++)
		{
			if(kws.containsKey(allKeyWords.get(x)))
			{
				if(keywordsIndex.containsKey(allKeyWords.get(x)))
				{
					ArrayList<Occurrence>occurrenceList = keywordsIndex.get(allKeyWords.get(x));
					occurrenceList.add(kws.get(allKeyWords.get(x)));
					insertLastOccurrence(occurrenceList);
				}
				else
				{
					ArrayList<Occurrence> occurrenceList = new ArrayList<Occurrence>();
					occurrenceList.add(kws.get(allKeyWords.get(x)));
					keywordsIndex.put(allKeyWords.get(x),occurrenceList);
				}
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		
		word = trimWord(word);
		
		if(word == null)
		{
			return null;
		}
		if(word.equals(""))
		{
			return null;
		}
	
		if(noiseWords.contains(word)==true)
		{
			return null;
		}
		else
		{
			return word;
		}
	}



	
	private static String trimWord(String word)
	{
		word = word.toLowerCase();

		for(int x = 0;x<word.length();x++)
		{
			if((word.charAt(x)<'A'||word.charAt(x)>'Z')&&(word.charAt(x)<'a'||word.charAt(x)>'z'))
			{
				if(x == word.length()-1)
				{
					return word.substring(0,word.length()-1);
				}
				else if((word.charAt(x+1)<'A'||word.charAt(x+1)>'Z')&&(word.charAt(x+1)<'a'||word.charAt(x+1)>'z'))
				{
					return word.substring(0,x);
				}
				else
				{
					return null;
				}
			}
		}
		if(word.equals("")||word.equals(" "))
		{
		return null;
		}
		else
		{
			return word;
		}
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */

	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		Occurrence value = occs.get(occs.size()-1);
		int freq = value.frequency;
		ArrayList<Integer>list = new ArrayList<Integer>();
		int max = 0;
		if(occs.size()>1)
		{
			max = occs.size()-2;
		}
		int min = 0;
		int mid = 0;
		occs.remove(occs.size()-1);
		while(true)
		{
			mid  =(min+max)/2;
			if(mid==max||mid==min||freq==occs.get(mid).frequency)
			{
				list.add(mid);
				if(freq>occs.get(mid).frequency)
				{
					occs.add(mid, value);
				}
				else
				{
					occs.add(mid+1,value);
				}
				break;
			}
			else if(freq<occs.get(mid).frequency)
			{
				min = mid+1;
				list.add(mid);
			}
			else if(freq>occs.get(mid).frequency)
			{
				max = mid-1;
				list.add(mid);
			}
		}
		return list;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}

		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<Occurrence>list1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence>list2 = new ArrayList<Occurrence>();
		ArrayList<String> answer = new ArrayList<String>();
		if(keywordsIndex.containsKey(kw1))
		{
			list1 = keywordsIndex.get(kw1);
		}
		if(keywordsIndex.containsKey(kw2))
		{
			list2=keywordsIndex.get(kw2);
		}
		while(true)
		{
			if(list1.isEmpty()==true&&list2.isEmpty()==true)
			{
				break;
			}
			if(answer.size()==5)
			{
				break;
			}
			else if(list2.isEmpty()==true)
			{
				if(answer.contains(list1.get(0).document)==false)
				{
					answer.add(list1.get(0).document);
				}
				list1.remove(0);
			}
			else if(list1.isEmpty()==true)
			{
				if(answer.contains(list2.get(0).document)==false)
				{
					answer.add(list2.get(0).document);
				}
				list2.remove(0);
			}
			else if(list1.get(0).frequency>=list2.get(0).frequency)
			{
				if(answer.contains(list1.get(0).document)==false)
				{
					answer.add(list1.get(0).document);
				}
				list1.remove(0);
			}
			else if(list1.get(0).frequency<list2.get(0).frequency)
			{
				if(answer.contains(list2.get(0).document)==false)
				{
					answer.add(list2.get(0).document);
				}
				list2.remove(0);
			}
		}
		return answer;
	}
}

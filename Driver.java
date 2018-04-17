package lse;
import java.io.FileNotFoundException;
import java.util.*;
public class Driver {

	public static void main(String[]args) throws FileNotFoundException
	{
		LittleSearchEngine search = new LittleSearchEngine(); 
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter File Name: ");
		String docsFile = scanner.nextLine();
		String noiseWordsFile = "noisewords.txt";
		search.makeIndex(docsFile, noiseWordsFile);
		System.out.println(search.top5search("alice",""));
	}
}
	


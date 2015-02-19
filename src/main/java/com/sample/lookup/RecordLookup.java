package com.sample.lookup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample application that matches records 
 * from an input file to the records specified in the dictionary file
 * 
 * @author erosenberg
 *
 */
public class RecordLookup {
	
	private static final Logger log = Logger.getLogger( RecordLookup.class.getName());
	
	private static final String dictionaryFilePath = "src/main/resources/dictionary.txt";
	private static final String inputFilePath = "src/main/resources/input.txt";
	private static final String matchedOutputFilePath = "src/main/resources/matchedOutput.txt";
	private static final String unmatchedOutputFilePath = "src/main/resources/unmatchedOutput.txt";
	
	
	public static void main(String[] args){

		Map<Integer, String> dictionary = loadDictionary();
		
		//Input file can be large, so buffered reader is more appropriate
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
			
			//Prepare output writers and keep them open during input file parsing
	        PrintWriter matchedOutput = new PrintWriter(new BufferedWriter(new FileWriter(matchedOutputFilePath, true)));
	        PrintWriter unmatchedOutput = new PrintWriter(new BufferedWriter(new FileWriter(unmatchedOutputFilePath, true)));
	        
	        String inputLine = reader.readLine();
	        
	        while (inputLine != null) {
	        	if(dictionary.containsValue(inputLine)){
	        		matchedOutput.println(getKeyByValue(dictionary, inputLine) + " " + inputLine);
	        		log.info("matched record " + inputLine);
	        	} else {
	        		unmatchedOutput.println(inputLine);
	        	}
	        	
	            inputLine = reader.readLine();
	        }
	        
	        matchedOutput.close();
	        unmatchedOutput.close();
	        
	    } catch(IOException e){
	    	log.log(Level.SEVERE, "Exception reading input file", e);
	    } 
	}
	
	/*
	 * Helper method that reads dictionary records from file into a map
	 */
	private static Map<Integer, String> loadDictionary() {
		log.info("Loading dictionary records");
		
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		List<String> lines = null;
		//Dictionary file is not expected to be large, so we can read entire file at once
		try {
			lines = Files.readAllLines(Paths.get(dictionaryFilePath));
		} catch (IOException e) {
			log.log(Level.SEVERE, "Exception reading dictionary file", e);
		}
		
		for(String dictionaryRecordLine : lines){
			//Dictionary records are space delimited, 
			//first element is index, second element is string value
			String[] dictionaryRecord = dictionaryRecordLine.split(" ");
			int index = Integer.parseInt(dictionaryRecord[0]);
			String value = dictionaryRecord[1];
			dictionary.put(index, value);
			log.info("Read dictionary values: " + index + " " + value );
		}
		
		log.info("Successfully loaded " + dictionary.size() + " records from dictionary file");
		return dictionary;
	}
	
	
	/*
	 * Helper method that retrieves a key from the map using a corresponding value
	 * This method assumes that map has 1 to 1 relationship between keys and values 
	 */
	private static Integer getKeyByValue(Map<Integer, String> map, String value) {
	    for (Entry<Integer, String> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    throw new RuntimeException("Could not find matching index in the dictionary for the value " + value);
	}
}

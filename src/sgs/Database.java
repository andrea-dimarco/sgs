package sgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import java.io.FileReader;
import java.io.IOException;
 
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Database 
{
	
	private static final HashMap<String, Integer> GROUPS = new HashMap<String, Integer>() 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("INTJ", 1);
			put("INTP", 1);
			put("ENTJ", 1);
			put("ENTP", 1);
			
			put("INFJ", 2);
			put("INFP", 2);
			put("ENFJ", 2);
			put("ENFP", 2);
			
			put("ISTJ", 3);
			put("ISFJ", 3);
			put("ESTJ", 3);
			put("ESFJ", 3);
			
			put("ISTP", 4);
			put("ISFP", 4);
			put("ESTP", 4);
			put("ESFP", 4);
		}
	};
		
	private static final HashMap<String, String> PERSONALITY_NAMES = new HashMap<String, String>() 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("INTJ", "Architect");
			put("INTP", "Logician");
			put("ENTJ", "Commander");
			put("ENTP", "Debater");
			
			put("INFJ", "Advocate");
			put("INFP", "Mediator");
			put("ENFJ", "Protagonist");
			put("ENFP", "Campaigner");
			
			put("ISTJ", "Logistician");
			put("ISFJ", "Defender");
			put("ESTJ", "Executive");
			put("ESFJ", "Consul");
			
			put("ISTP", "Virtuoso");
			put("ISFP", "Adventurer");
			put("ESTP", "Entrepeneur");
			put("ESFP", "Entertainer");
		}
	};
	
	private static HashMap<String, String> DESCRIPTION = new HashMap<String, String>();
	private static ArrayList<String> NAMES;
	
	@SuppressWarnings("unchecked")
	public static void readFiles()
	{
		JSONParser jsonParser = new JSONParser();
		
        try (FileReader reader = new FileReader("names.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            NAMES = (ArrayList<String>) obj;
        }
        catch (IOException | ParseException e) {}
        
        try (FileReader reader = new FileReader("persDatabase.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            DESCRIPTION = (HashMap<String, String>) obj;
        }
        catch (IOException | ParseException e) {}
	}
 
	
	// Metodi ////////////////////////////////////////////
	
	/**
	 * Ritorna il gruppo a cui appartiene la Cell in base alla sua personalit‡
	 * @param personality
	 * @return int 1, 2, 3 o 4
	 */
	public static int getGroup(String personality)
	{
		return GROUPS.get(personality);
	}
	
	/**
	 * Ritorna la stringa del nome, scelta randomicamente tra l'array
	 * @return 
	 */
	public static String getRandomName()
	{
		Random rand = new Random();
		int i = rand.nextInt(NAMES.size());
		return NAMES.get(i);
	}
	
	/**
	 * Ritorna il nome della personalit‡
	 * @param personality
	 * @return
	 */
	public static String getPersonalityName(String personality)
	{
		return PERSONALITY_NAMES.get(personality);
	}
	
	public static String[] getPersonalities()
	{
		String[] array = {"INTJ", "INTP", "ENTJ", "ENTP",
							"INFJ", "INFP", "ENFJ", "ENFP",
							"ISTJ", "ISFJ", "ESTJ", "ESFJ",
							"ISTP", "ISFP", "ESTP", "ESFP"};
		return array;
	}
	
	public static String getDescription(String personality)
	{
		return DESCRIPTION.get(personality);
	}
}


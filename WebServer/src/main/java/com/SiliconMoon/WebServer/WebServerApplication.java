package com.SiliconMoon.WebServer;

import java.sql.Connection; 
import java.sql.Statement; 
import java.sql.DriverManager; 
import java.sql.DatabaseMetaData; 
import java.sql.SQLException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebServerApplication {

/* 

createNewDatabase
	:parameters:
		filename: String

	:returns:

	:Description: 
		creates a SQL database using Derby. 

*/

	public static void createNewDatabase(String fileName) 
	{  
   
        	String url = "jdbc:derby:./" + fileName + ";create=true";  
   
        	try 
		{  
            		Connection conn = DriverManager.getConnection(url); 
 
            		if (conn != null) 
			{  
                		DatabaseMetaData meta = conn.getMetaData();  
                		System.out.println("The driver name is " + meta.getDriverName());  
                		System.out.println("A new database has been created.");  
            		}  
   
        	} 
		catch (SQLException e) 
		{  
            		System.out.println(e.getMessage()); 
        	}  
    	} 


	 
/* 

connect
	:parameters:
	:returns:

	:Description: 
		Used to connect to the database. You may need to call this in other classes.  

*/
	public static void connect() 
	{  
        	Connection conn = null;  
        
		try 
		{  
            		// db parameters  
            		String url = "jdbc:derby:./new.db";  
            		// create a connection to the database  
            		conn = DriverManager.getConnection(url);  
              
            		System.out.println("Connection to SQLite has been established.");  

			getData(conn);
              
        	} 
		catch (Exception e) 
		{  
            		System.out.println(e.getMessage());  
        	} 
		finally 
		{  
            		try 
			{  
                		if (conn != null) 
				{  
                    			conn.close();  
                		}  
            		} 
			catch (SQLException ex) 
			{  
                		System.out.println(ex.getMessage());  
            		}  
        	} 
	} 



/* 
getData
	:parameters:
		conn: Connection (Data type for derby)

	:returns:

	:Description: 
		* Creates a table "movies" in the "new" database
		* Grabs the .csv file and parses through it to insert it into the table movies
*/
	public static void getData(Connection conn) 
	{
		String line = "";
		
	
		//Buffers through the file "Oscar_Winner_data_csv.csv"
		try (BufferedReader in = new BufferedReader(new FileReader("./src/main/resources/Oscar_Winner_data_csv.csv"))) 
		{
			Statement statement = conn.createStatement();
			String sql = "";

			//read the first line of the file
			line = in.readLine();
			// separate that first line by the commas
			String[] labels = line.split(",");
			
						
			//This will create the table with the necessary columns
			System.out.println("Creating Table...");
			sql = "CREATE TABLE movies (number INT PRIMARY KEY, \"" + labels[0] + "\" INT";
			for(int i = 1; i < labels.length-1; i++)
			{
				sql += ", " + labels[i] + " VARCHAR(512)";
			}

			sql += ", " + labels[labels.length-1] + " VARCHAR(1024)";
			sql += ")";
			statement.execute(sql);	


	

			int x = 1;

			System.out.println("Populating the Table. This may take a while...");
			//Populating the table
    			while ((line = in.readLine()) != null)
			{

				sql = "INSERT INTO movies (number, \"year\", category, winner, entity) VALUES ";
				String[] value = line.split(",", labels.length);
				sql += "(" + x + ", "+ value[0] + ",";

				for (int i=1; i < value.length-1; i++)
				{
					sql += " \'" + value[i] + "\', ";
				}
				
				sql += " \'" + value[value.length-1].replace("'", "\'\'") + "\')";
				x++;
		
				statement.execute(sql);
				
			}
			
			//close the statement. Will need to be reopened if a query is being made or new data is being added. 
    			statement.close();
			System.out.println("Server up and running! HI!");
			
		}
		catch (Exception e)
		{
			//dropping table for testing purposes
			try
			{
				Statement statement = conn.createStatement();
				statement.execute("DROP TABLE movies");	
				statement.close();
				System.out.println("Table Dropped, Please run again.");
			} catch (Exception e2)
			{
    				System.out.println(e2);
			}
			System.out.println(e);
		}
		return;	
	}


	public static void main(String[] args) {
		SpringApplication.run(WebServerApplication.class, args);

		try
		{
			createNewDatabase("new.db");
		}
		finally
		{
			connect();
		}
	}

}

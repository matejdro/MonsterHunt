package com.matejdro.bukkit.monsterhunt;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

public class InputOutput {
private static Connection connection;
	
	
	 public static synchronized Connection getConnection() {
	        try {
				if (connection == null || connection.isClosed()) {
				    connection = createConnection();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return connection;
	    }

	    private static Connection createConnection() {
	        try {
	            if (Settings.globals.getBoolean("Database.UseMySQL", false)) {
	                Class.forName("com.mysql.jdbc.Driver");
	                Connection ret = DriverManager.getConnection(Settings.globals.getString("Database.MySQLConn", ""), Settings.globals.getString("Database.MySQLUsername", ""), Settings.globals.getString("Database.MySQLPassword", ""));
	                ret.setAutoCommit(false);
	                return ret;
	            } else {
	                Class.forName("org.sqlite.JDBC");
	                Connection ret = DriverManager.getConnection("jdbc:sqlite:plugins" + File.separator + "MonsterHunt" + File.separator + "MonsterHunt.sqlite");
	                ret.setAutoCommit(false);
	                return ret;
	            }
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	            return null;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    	    
	    public static Integer getHighScore(String player)
	    {
	    	Connection conn = getConnection();
	    	PreparedStatement ps = null;
			ResultSet set = null;
			Integer score = null;
			
	    	try {
				ps = conn.prepareStatement("SELECT * FROM monsterhunt_highscores WHERE name = ? LIMIT 1");
			
            ps.setString(1, player);
            set = ps.executeQuery();
            
            if (set.next())
            	score = set.getInt("highscore");
            
            set.close();
            ps.close();
            conn.close();
	    	} catch (SQLException e) {
	    		MonsterHunt.log.log(Level.SEVERE,"[MonsterHunt] Error while retreiving high scores! - " + e.getMessage() );
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return score;
            
	    }
	    
	    public static Integer getHighScoreRank(String player)
	    {
	    	Connection conn = getConnection();
	    	PreparedStatement ps = null;
			ResultSet set = null;
			Boolean exist = false;
            Integer counter = 0;
            
	    	try {
				ps = conn.prepareStatement("SELECT * FROM monsterhunt_highscores ORDER BY highscore DESC");
			
            set = ps.executeQuery();
            
            
            while (set.next())
            {
            	counter++;
            	String name = set.getString("name");
            	if (name.equals(player))
            	{
            		exist = true;
            		break;
            	}
            }
            
            set.close();
            ps.close();
            conn.close();
            
	    	} catch (SQLException e) {
	    		MonsterHunt.log.log(Level.SEVERE,"[MonsterHunt] Error while retreiving high scores! - " + e.getMessage() );
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if (exist)
            	return counter;
            else
            	return null;
	    }
	    
	    public static LinkedHashMap<String, Integer> getTopScores(int number)
	    {
	    	Connection conn = getConnection();
	    	PreparedStatement ps = null;
			ResultSet set = null;
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
            
	    	try {
				ps = conn.prepareStatement("SELECT * FROM monsterhunt_highscores ORDER BY highscore DESC LIMIT ?");
				ps.setInt(1, number);
				
	            set = ps.executeQuery();
	            
	            
	            while (set.next())
	            {
	            	
	            	String name = set.getString("name");
	            	Integer score = set.getInt("highscore");
	            	map.put(name, score);
	            }
	            
	            set.close();
	            ps.close();
	            conn.close();
            
	    	} catch (SQLException e) {
	    		MonsterHunt.log.log(Level.SEVERE,"[MonsterHunt] Error while retreiving high scores! - " + e.getMessage() );
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return map;
	    }
	    
	    public static void UpdateHighScore(String playername, int score)
	    {
	    	try {
				Connection conn = InputOutput.getConnection();
				PreparedStatement ps = conn.prepareStatement("REPLACE INTO monsterhunt_highscores VALUES (?,?)");
				ps.setString(1, playername);
				ps.setInt(2, score);
				ps.executeUpdate();
				conn.commit();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				MonsterHunt.log.log(Level.SEVERE,"[MonsterHunt] Error while inserting new high score into DB! - " + e.getMessage() );
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	
	public static void LoadSettings()
	{	
		if (!new File("plugins" + File.separator + "MonsterHunt").exists()) {
			try {
			(new File("plugins" + File.separator + "MonsterHunt")).mkdir();
			} catch (Exception e) {
			MonsterHunt.log.log(Level.SEVERE, "[MonsterHunt]: Unable to create plugins/MontsterHunt/ directory");
			}
			}
		Settings.globals = new Configuration(new File("plugins" + File.separator + "MonsterHunt" + File.separator, "global.txt"));

		LoadDefaults();
		
		Settings.globals.load();
		
		for (String n : Settings.globals.getString("EnabledWorlds").split(","))
		{
					MonsterHuntWorld mw = new MonsterHuntWorld(n);
					Configuration config = new Configuration(new File("plugins" + File.separator + "MonsterHunt" + File.separator,n + ".yml"));
					Settings settings = new Settings(config);
					mw.settings = settings;
				
					HuntWorldManager.worlds.put(n, mw);
		}
		
		String[] temp = Settings.globals.getString("HuntZone.FirstCorner", "0,0,0").split(",");
		HuntZone.corner1 = new Location(null, Double.parseDouble(temp[0]), Double.parseDouble(temp[1]), Double.parseDouble(temp[2]));
		temp = Settings.globals.getString("HuntZone.SecondCorner", "0,0,0").split(",");
		HuntZone.corner2 = new Location(null, Double.parseDouble(temp[0]), Double.parseDouble(temp[1]), Double.parseDouble(temp[2]));
		temp = Settings.globals.getString("HuntZone.TeleportLocation", "0,0,0").split(",");
		World world = MonsterHunt.instance.getServer().getWorld(Settings.globals.getString("HuntZone.World", MonsterHunt.instance.getServer().getWorlds().get(0).getName()));
		HuntZone.teleport = new Location(world, Double.parseDouble(temp[0]), Double.parseDouble(temp[1]), Double.parseDouble(temp[2]));
		
		//Create zone world
		MonsterHuntWorld mw = new MonsterHuntWorld(world.getName());
		Configuration config = new Configuration(new File("plugins" + File.separator + "MonsterHunt" + File.separator + "zone.yml"));
		Settings settings = new Settings(config);
		mw.settings = settings;
	
		HuntWorldManager.HuntZoneWorld = mw;
		
	}
	
	public static void LoadDefaults()
	{
		if (!new File("plugins" + File.separator + "MonsterHunt" + File.separator, "global.txt").exists()) 
		{
			for (String i : new String[]{"Zombie", "Skeleton", "Creeper", "Spider", "Ghast", "Slime", "ZombiePigman", "Giant", "TamedWolf", "WildWolf", "ElectrifiedCreeper", "Player", "Enderman", "Silverfish", "CaveSpider"})
			{
				Settings.globals.setProperty("Value." + i + ".General", 10);
				Settings.globals.setProperty("Value." + i + ".Wolf", 7);
				Settings.globals.setProperty("Value." + i + ".Arrow", 4);
				Settings.globals.setProperty("Value." + i + ".283", 20);
			}
			
			Settings.globals.setProperty("MinimumPointsPlace1", 1);
			Settings.globals.setProperty("MinimumPointsPlace2", 1);
			Settings.globals.setProperty("MinimumPointsPlace3", 1);
			Settings.globals.setProperty("Rewards.RewardParametersPlace1", "3 3");
			Settings.globals.setProperty("Rewards.RewardParametersPlace2", "3 2");
			Settings.globals.setProperty("Rewards.RewardParametersPlace3", "3 1");
		}
		
		for (Setting s : Setting.values())
    	{
    		if (s.writeDefault() && Settings.globals.getProperty(s.getString()) == null) Settings.globals.setProperty(s.getString(), s.getDefault());
    	}
	
		Settings.globals.save();
		
	}
	
	public static void saveZone()
	{
		Settings.globals.setProperty("HuntZone.FirstCorner", String.valueOf(HuntZone.corner1.getBlockX()) + "," + String.valueOf(HuntZone.corner1.getBlockY()) + "," + String.valueOf(HuntZone.corner1.getBlockZ()));
		Settings.globals.setProperty("HuntZone.SecondCorner", String.valueOf(HuntZone.corner2.getBlockX()) + "," + String.valueOf(HuntZone.corner2.getBlockY()) + "," + String.valueOf(HuntZone.corner2.getBlockZ()));
		Settings.globals.setProperty("HuntZone.TeleportLocation", String.valueOf(HuntZone.teleport.getX()) + "," + String.valueOf(HuntZone.teleport.getY()) + "," + String.valueOf(HuntZone.teleport.getZ()));
		Settings.globals.setProperty("HuntZone.World", HuntZone.teleport.getWorld().getName());
		
		Settings.globals.save();
	}
		
	public static void PrepareDB()
    {
        Connection conn = null;
        Statement st = null;
        try {
            conn = InputOutput.getConnection();
            st = conn.createStatement();
            if (Settings.globals.getBoolean("Database.UseMySQL", false))
            {
            	st.executeUpdate("CREATE TABLE IF NOT EXISTS `monsterhunt_highscores` ( `name` varchar(250) NOT NULL DEFAULT '', `highscore` integer DEFAULT NULL, PRIMARY KEY (`name`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            }
            else
            {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS \"monsterhunt_highscores\" (\"name\" VARCHAR PRIMARY KEY  NOT NULL , \"highscore\" INTEGER)");
  	
            }
                conn.commit();
        } catch (SQLException e) {
            MonsterHunt.log.log(Level.SEVERE, "[MonsterHunt]: Error while creating tables! - " + e.getMessage());
    }
    }
	
}

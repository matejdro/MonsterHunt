package com.matejdro.bukkit.monsterhunt;

import java.util.Arrays;


public enum Setting {
	
	StartTime("StartTime", 13000),
	EndTime("EndTime", 23600),
	DeathPenalty("DeathPenalty", 30),
	TellTime("TellTime", true),
	EnableSignup("EnableSignup", true),
	MinimumPlayers("MinimumPlayers", 2),
	StartChance("StartChance", 100),
	SkipDays("SkipDays", 0),
	SignUpPeriodTime("SignUpPeriodTime", 5),
	AllowSignUpAfterStart("AllowSignUpAfterStart", false),
	EnabledWorlds("EnabledWorlds", MonsterHunt.instance.getServer().getWorlds().get(0).getName()),
	OnlyCountMobsSpawnedOutside("OnlyCountMobsSpawnedOutside", false),
	OnlyCountMobsSpawnedOutsideHeightLimit("OnlyCountMobsSpawnedOutsideHeightLimit", 0),
	OnlyCountMobsSpawnedOutsideBlackList("OnlyCountMobsSpawnedOutsideBlackList", true),
	SkipToIfFailsToStart("SkipToIfFailsToStart", -1),
	AnnounceLead("AnnounceLead", true),
	SelectionTool("SelectionTool", 268),
	HuntZoneMode("HuntZoneMode", false),
	AnnounceSignUp("AnnounceSignUp", true),
	
	EnableReward("Rewards.EnableReward", true),
	EnableRewardEveryonePermission("Rewards.EnableRewardEveryonePermission", false),
	RewardEveryone("Rewards.RewardEveryone", false),
	NumberOfWinners("Rewards.NumberOFWinners", 3),
	RewardParametersEveryone("Rewards.RewardParametersEveryone", "3 1-1"),
	MinimumPointsEveryone("MinimumPointsEveryone", 1),
	MinimumPointsPlace("MinimumPointsPlace", "", false),
	RewardParametersPlace("Rewards.RewardParametersPlace", "", false),
	
	
	UseMySQL("Database.UseMySQL", false),
	MySQLConn("Database.MySQLConn", "jdbc:mysql://localhost:3306/minecraft"),
	MySQLUsername("Database.MySQLUsername", "root"),
	MySQLPassword("Database.MySQLPassword", "password"),
	
	Debug("Debug", false),
	
	HuntZoneFirstCorner("HuntZone.FirstCorner", "0,0,0"),
	HuntZoneSecondCorner("HuntZone.SecondCorner", "0,0,0"),
	HuntZoneTeleportLocation("HuntZone.TeleportLocation", "0,0,0"),
	HuntZoneWorld("HuntZone.World", MonsterHunt.instance.getServer().getWorlds().get(0).getName()),
	
	StartMessage("Messages.StartMessage", "&2Monster Hunt have started in world <World>! Go kill those damn mobs!"),
	FinishMessageWinners("Messages.FinishMessageWinners", "Sun is rising, so monster Hunt is finished in world <World>! Winners of the today's match are: [NEWLINE] 1st place: <NamesPlace1> (<PointsPlace1> points) [NEWLINE] 2nd place: <NamesPlace2> (<PointsPlace2> points) [NEWLINE] 3rd place: <NamesPlace3> (<PointsPlace3> points)" ),
	KillMessageGeneral("Messages.KillMessageGeneral", "You have got <MobValue> points from killing that <MobName>. You have <Points> points so far. Keep it up!"),
	KillMessageWolf("Messages.KillMessageWolf", "You have got <MobValue> points because your wolf killed <MobName>. You have <Points> points so far. Keep it up!"),
	KillMessageArrow("Messages.KillMessageArrow", "You have got only <MobValue> points because you used bow when killing <MobName>. You have <Points> points so far. Keep it up!"),
	RewardMessage("Messages.RewardMessage", "Congratulations! You have received <Items>"),
	DeathMessage("Messages.DeathMessage","You have died, so your Monster Hunt score is reduced by 30%. Be more careful next time!"),
	NoBowMessage("Messages.NoBowMessage", "Your kill is not counted. Stop camping with your bow and get into the fight!"),
	SignUpBeforeHuntMessage("Messages.SignupBeforeHuntMessage", "You have signed up for the next hunt in world <World>!"),
	SignUpAfterHuntMessage("Messages.SignupAtHuntMessage", "You have signed up for the hunt in in world <World>. Now hurry and kill some monsters!"),
	HighScoreMessage("Messages.HighScoreMessage","You have reached a new high score: <Points> points!"),
	FinishMessageNotEnoughPoints("Messages.FinishMessageNotEnoughPoints", "Sun is rising, so monster Hunt is finished in world <World>! Unfortunately nobody killed enough monsters, so there is no winner."),
	FinishMessageNotEnoughPlayers("Messages.FinishMessageNotEnoughPlayers", "Sun is rising, so monster Hunt is finished in world <World>! Unfortunately there were not enough players participating, so there is no winner."),
	MessageSignUpPeriod("Messages.MessageSignUpPeriod", "Sharpen your swords, strengthen your armor and type /hunt, because Monster Hunt will begin in several mintues in world <World>!"),
	MessageTooLateSignUp("Messages.MessageTooLateSignUp", "Sorry, you are too late to sign up. More luck next time!"),
	MessageAlreadySignedUp("Messages.MessageAlreadySignedUp", "You are already signed up!"),
	MessageStartNotEnoughPlayers("Messages.MessageStartNotEnoughPlayers", "Monster Hunt was about to start, but unfortunately there were not enough players signed up. "),
	KillMobSpawnedInsideMessage("Messages.KillMobSpawnedInsideMessage", "Your kill was not counted. Stop grinding in caves and go outside!"),
	MessageHuntStatusNotActive("Messages.MessageHuntStatusNotActive", "Hunt is currently not active anywhere"),
	MessageHuntStatusHuntActive("Messages.MessageHuntStatusHuntActive", "Hunt is active in <Worlds>"),
	MessageHuntStatusLastScore("Messages.MessageHuntStatusLastScore", "Your last score in this world was <Points> points"),
	MessageHuntStatusNotInvolvedLastHunt("Messages.MessageHuntStatusNotInvolvedLastHunt", "You were not involved in last hunt in this world"),
	MessageHuntStatusNoKills("Messages.MessageHuntStatusNoKills", "You haven't killed any mob in this world's hunt yet. Hurry up!"),
	MessageHuntStatusCurrentScore("Messages.MessageHuntStatusCurrentScore", "Your current score in this world's hunt is <Points> points! Keep it up!"),
	MessageHuntStatusTimeReamining("Messages.MessageHuntStatusTimeReamining", "Keep up the killing! You have only <Timeleft>% of the night left in this world!"),
	MessageLead("Messages.MessageLead", "<Player> has just taken over lead with <Points> points!"),
	MessageHuntTeleNoHunt("Messages.MessageHuntTeleNoHunt", "You cannot teleport to hunt zone when there is no hunt!"),
	MessageHuntTeleNotSignedUp("Messages.MessageHuntTeleNotSignedUp", "You cannot teleport to hunt zone if you are not signed up to the hunt!"),
	SignUpAnnouncement("Messages.SignUpAnnouncement", "<Player> has signed up for the hunt in world <World>!");

	private String name;
	private Object def;
	private Boolean WriteDefault;
	
	private Setting(String Name, Object Def)
	{
		name = Name;
		def = Def;
		WriteDefault = true;
	}
	
	private Setting(String Name, Object Def, Boolean WriteDefault)
	{
		name = Name;
		def = Def;
		this.WriteDefault = WriteDefault;
	}
	
	public String getString()
	{
		return name;
	}
	
	public Object getDefault()
	{
		return def;
	}
	
	public Boolean writeDefault()
	{
		return WriteDefault;
	}
}

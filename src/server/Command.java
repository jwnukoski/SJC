package server;

// Just used to describe functions in /help
public class Command {
	private String cmdWord = "";
	private String description = "";
	
	public Command(String _cmdWord, String _description) {
		cmdWord = _cmdWord;
		description = _description;
	}
	
	public String getCmdWord() {
		return cmdWord;
	}
	
	public String getDescription() {
		return description;
	}
}

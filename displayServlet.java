
public class displayServlet {
	static private Database db = new Database();
	private FormGenerator formGenerator = new FormGenerator();
	public void databaseShow() {
		String name = formGenerator.findNameForm();
    	db.showDB(name);
    	
    }
}

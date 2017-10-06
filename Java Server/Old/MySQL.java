import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;


public class MySQL {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	public MySQL() throws Exception {
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/bout_evolution?"
							+ "user=admin&password=admin");
			PreparedStatement statement = connect
					.prepareStatement("SELECT * from BOUT_EVOLUTION.BOUT_USERS");

			resultSet = statement.executeQuery();
			while (resultSet.next()) {
                int LOGIN_ID = resultSet.getInt("id");
				String LOGIN_USERNAME = resultSet.getString("username");
				String LOGIN_PASSWORD = resultSet.getString("password");
				int LOGIN_BANNED = resultSet.getInt("banned");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}


	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

	public static void main(String[] args) throws Exception {
		MySQL dao = new MySQL();
	}

}

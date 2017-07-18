package blanja.temp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JavaTreeRecursively {

	private Connection con;
	private String host;
	private String database;
	private String user;
	private String password;
	
	private Connection getConnection() throws SQLException,
			ClassNotFoundException {
		if(con == null){
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"+host+":3306/" + database, user, password);
		}
		return con;
	}

	private void close(Connection c) throws SQLException {
//		c.close();
	}

	public JavaTreeRecursively() throws ClassNotFoundException, SQLException {
		List<Node> result = new ArrayList<DigitalProduct.Node>();
		List<Node> nodes = trace(new Node(1, 0, "Category", null, null), result);
		System.out.println(nodes.size());
		
		for(Node n : nodes){
			System.out.print(n.getId() + ",");
		}
	}
	
	private List<Node> trace(Node node, List<Node> result) throws ClassNotFoundException, SQLException{
		if(node != null){
			System.out.println("trace: " + node.getId() + ";" + node.getName());
			result.add(node);
			List<Node> nodes = getChilds(node, result);
			if(nodes.size() > 0){
				return trace(nodes.get(0), result);
			}
			else if(node.getNext() != null){
				return trace(node.getNext(), result);
			}
			else if(node.getParent() != null){
				trace(node.getParent(), result);
			}
		}
		return result;
	}
	
	private String constructNotIn(List<Node> result){
		String r = "";
		if(result != null){
			int i = 0;
			for(Node n : result){
				r += n.getId() + ((result.size() - 1) == i ? "" : ",");
				i++;
			}
			return r;
		}
		return "0";
	}

	private List<Node> getChilds(Node node, List<Node> result) throws SQLException, ClassNotFoundException {
		List<Node> nodes = new ArrayList<Node>();
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		String r = constructNotIn(result);
		String sql = "select id, parent_id, name from category where parent_id = " + node.getId() + " and id NOT IN ("+r+") order by id asc";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()){
			Node n = new Node(rs.getInt(1), rs.getInt(2), rs.getString(3), node, null);
			nodes.add(n);
		}
			
		// set next
		int i = 0;
		for(Node n : nodes){
			n.setNext(i <= (nodes.size() - 2) ? nodes.get(i + 1) : null);
			i++;
		}
		close(con);
		return nodes;
	}

	private class Node {
		private int id;
		private int parentId;
		private Node parent;
		private Node next;
		private String name;

		public Node(int id, int parentId, String name, Node parent, Node next){
			this.id = id;
			this.parentId = parentId;
			this.parent = parent;
			this.next = next;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getParentId() {
			return parentId;
		}

		public void setParentId(int parentId) {
			this.parentId = parentId;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public Node getNext() {
			return next;
		}

		public void setNext(Node next) {
			this.next = next;
		}
		
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		new DigitalProduct();
	}
}

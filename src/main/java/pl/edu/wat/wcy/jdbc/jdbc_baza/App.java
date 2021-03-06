package pl.edu.wat.wcy.jdbc.jdbc_baza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class App extends JFrame implements ActionListener {

	private static String dbName;
	private static String userName;
	private static String password;

	protected JButton button;
	protected JTable table;

	public App() {

		table = new JTable();

		try {
			table.setModel(loadData());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane);

		button = new JButton("Refresh");
		getContentPane().add(button, BorderLayout.SOUTH);
		button.addActionListener(this);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setHeaderValue("Nazwa panstwa");
		table.getColumnModel().getColumn(1).setHeaderValue("Przychod netto USD");
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	}

	public static void main(String[] args) {
		App frame = new App();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(250, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	protected TableModel loadData() throws ClassNotFoundException {

		Vector<Object> columnNames = new Vector<Object>();
		Vector<Object> data = new Vector<Object>();

		Class.forName("org.postgresql.Driver");

		try {
			Connection c = null;
			Statement stmt = null;

			BufferedReader br = new BufferedReader(new FileReader("login.txt"));
			dbName = br.readLine();
			userName = br.readLine();
			password = br.readLine();

			c = DriverManager.getConnection(dbName, userName, password);
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			String query = "SELECT C.country, SUM(O.netamount) from customers as C join orders as O "
					+ "on C.customerid = O.customerid where O.orderdate < '20040701' and O.orderdate > '20031231' "
					+ "group by C.country order by SUM(O.netamount) DESC";
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();

			for (int i = 1; i <= columns; i++) {
				columnNames.addElement(md.getColumnName(i));
			}

			while (rs.next()) {
				Vector<Object> row = new Vector<Object>(columns);

				for (int i = 1; i <= columns; i++) {
					row.addElement(rs.getObject(i));
				}

				data.addElement(row);
			}
		}

		catch (Exception e) {
			System.out.println(e);
		}
		return new DefaultTableModel(data, columnNames) {
			@Override
			public Class getColumnClass(int column) {
				for (int row = 0; row < getRowCount(); row++) {
					Object o = getValueAt(row, column);

					if (o != null) {
						return o.getClass();
					}
				}
				return Object.class;
			}
		};
	}

	public void actionPerformed(ActionEvent arg0) {
		try {
			table.setModel(loadData());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setHeaderValue("Nazwa panstwa");
		table.getColumnModel().getColumn(1).setHeaderValue("Przychod netto USD");
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	}
}

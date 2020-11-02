package gestaldatabase;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class SqlToCsv extends JFrame {

    public final String dbDir = (System.getProperty("user.dir") + File.separator + "GestalDB");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new SqlToCsv(args);
    }

    public SqlToCsv(String[] args) {
        if(args!=null && args.length!=0)
            CLI(args);
        else
            GUI();
    }
    
    private void CLI(String[] args){
        try{
            if(args.length == 3){
                int Result = ExportToCsv(args[0], args[1], new File(args[2] + ".csv"));
                System.exit(Result);
            }
            else
                System.exit(1);
        }
        catch (Exception e) {
            System.exit(-1);
        }
    }
        
    private void GUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("SQL to CSV");

        JTextField SqlCommand = new JTextField("SELECT * FROM APP.TRUIE");
        SqlCommand.setPreferredSize(new Dimension(500, 27));

        JButton ButtonExport = new JButton("Export to CSV...");
        ButtonExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int Result = ExportToCsv(dbDir, SqlCommand.getText(), null);
                if(Result == 0)
                    JOptionPane.showMessageDialog(null, "Query successfully  exported",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setLayout(new FlowLayout());
        add(SqlCommand);
        add(ButtonExport);
        pack();
        setVisible(true);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2,
                dim.height / 2 - this.getSize().height / 2);
    }

    private int ExportToCsv(String DBPath, String Sql, File ExportFile) {
        int Result = -1;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;

        try {
            if(ExportFile == null)
                ExportFile = GetExportationFile();
            if (ExportFile != null) {
                Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
                connection = DriverManager.getConnection(
                        "jdbc:derby:" + DBPath + File.separator + "GestalSS;user=guest");
                statement = connection.createStatement();
                resultSet = statement.executeQuery(Sql);

                StringBuffer stringBuffer = new StringBuffer();

                ResultSetMetaData metadata = resultSet.getMetaData();
                for (int i = 1; i < metadata.getColumnCount() + 1; i++) {
                    stringBuffer.append(metadata.getColumnName(i) + ", ");
                }
                stringBuffer.append(System.lineSeparator());

                while (resultSet.next()) {
                    for (int i = 1;; i++) {
                        try {
                            Object O = resultSet.getObject(i);
                            stringBuffer.append(O + ", ");
                        }
                        catch (Exception ex) {
                            break;
                        }
                    }
                    stringBuffer.append(System.lineSeparator());
                }

                fileOutputStream = new FileOutputStream(ExportFile);
                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write(stringBuffer.toString());
                
                Result = 0;
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
                if (outputStreamWriter != null)
                    outputStreamWriter.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            }
            catch (Exception e) {
            }
        }
        return Result;
    }

    private File GetExportationFile() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            file = new File(fileChooser.getSelectedFile().toString() + ".csv");
        return file;
    }
}

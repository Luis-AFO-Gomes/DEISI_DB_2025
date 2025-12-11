package pt.ulusofona.aed.deisimdb;

import javax.swing.*;
// import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ---- Table Model ----
/*
class ActorsTableModel extends AbstractTableModel {
    private final String[] columns = {"Nome", "ID", "Género"};
    private List<Actor> data = new ArrayList<>();

    public void setData(List<Actor> data) {
        this.data = data != null ? data : new ArrayList<>();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Actor a = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> a.getName();
            case 1 -> a.getActorId();
            case 2 -> a.getGeneroLongo();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 1 ? Integer.class : String.class;
    }
}
*/
// ---- UI ----
public class ActorsSwingApp extends JFrame {

    private final Dao dao = new Dao(); // your existing class
    private final ActorsTableModel model = new ActorsTableModel();
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel(" ");

    public ActorsSwingApp() {
    super("Listar Actores Ativos");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(600, 400));
    setLocationRelativeTo(null);

    // Table tweaks
    table.setFillsViewportHeight(true);
    table.setRowHeight(24);
    table.setAutoCreateRowSorter(true);

    JButton btnLoad = new JButton("Listar actores activos");
    btnLoad.addActionListener(e -> loadActors());

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    top.add(btnLoad);

    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    bottom.add(status, BorderLayout.WEST);

    JPanel listPanel = new JPanel(new BorderLayout(8, 8));
    listPanel.add(top, BorderLayout.NORTH);
    listPanel.add(new JScrollPane(table), BorderLayout.CENTER);
    listPanel.add(bottom, BorderLayout.SOUTH);

    AddActorPanel addPanel = new AddActorPanel(this::loadActors);

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Listar", listPanel);
    tabs.addTab("Adicionar", addPanel);

    setContentPane(tabs);

    loadActors();
}

    private void loadActors() {
        status.setText("A carregar...");
        
        SwingWorker<List<Actor>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Actor> doInBackground() throws Exception {
                return fetchActiveActors();
            }

            @Override
            protected void done() {
                try {
                    List<Actor> actors = get();
                    model.setData(actors);
                    status.setText("Listagem concluída. Total: " + actors.size());
                } catch (Exception ex) {
                    model.setData(List.of());
                    status.setText("Erro ao listar actores.");
                    JOptionPane.showMessageDialog(
                            ActorsSwingApp.this,
                            "Database error: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private List<Actor> fetchActiveActors() throws SQLException {
        String sql = """
                SELECT actorID, actorName, actorGender
                FROM dbo.tf_getActiveActors()
                ORDER BY actorID DESC;
                """;

        List<Actor> list = new ArrayList<>();

        try (Connection connection = dao.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("actorID");
                String name = rs.getString("actorName");
                String gender = rs.getString("actorGender");

                String genderLabel = mapGender(gender);

                list.add(new Actor(id, name, gender.charAt(0),null));
            }
        }

        return list;
    }

    private String mapGender(String g) {
        if (g == null) return "—";
        return g.equalsIgnoreCase("F") ? "Feminino"
             : g.equalsIgnoreCase("M") ? "Masculino"
             : g; // fallback for unexpected values
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ActorsSwingApp().setVisible(true));
    }
}

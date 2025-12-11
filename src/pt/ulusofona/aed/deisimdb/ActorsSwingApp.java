package pt.ulusofona.aed.deisimdb;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



// ---- UI ----
public class ActorsSwingApp extends JFrame {

    private final Dao dao = new Dao();
    private final ActorsTableModel model = new ActorsTableModel();
    private final AddActorPanel addPanel = new AddActorPanel(this::loadActors);
    private final UpdateActorPanel updatePanel = new UpdateActorPanel(this::loadActors);
    private final DeleteActorPanel deletePanel = new DeleteActorPanel(this::loadActors);

    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel(" ");
    private final JTabbedPane tabs = new JTabbedPane();;

    public ActorsSwingApp() {
        super("Gestão de Actores - DEISI IMDB");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        // Table tweaks
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);

        JButton btnLoad = new JButton("Listar actores");
        btnLoad.addActionListener(e -> loadActors());
        JButton btnEdit = new JButton("Editar actor seleccionado");
        btnEdit.addActionListener(e -> openSelectedActorInUpdateTab());
        JButton btnDelete = new JButton("Eliminar actor seleccionado");
        btnDelete.addActionListener(e -> openSelectedActorInDeleteTab());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btnLoad);
        top.add(btnEdit);
        top.add(btnDelete);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        bottom.add(status, BorderLayout.WEST);

        JPanel listPanel = new JPanel(new BorderLayout(8, 8));
        listPanel.add(top, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        listPanel.add(bottom, BorderLayout.SOUTH);

//        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Listar", listPanel);
        tabs.addTab("Adicionar", addPanel);
        tabs.addTab("Atualizar", updatePanel);
        tabs.addTab("Eliminar", deletePanel);  

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

    private void openSelectedActorInUpdateTab() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione um actor na tabela primeiro.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Se a tabela estiver ordenada, converter índice de view para índice de modelo
        int modelRow = table.convertRowIndexToModel(row);

        // Na ActorsTableModel: coluna 1 é o ID (0 = Nome, 1 = ID, 2 = Género)
        int actorId = (Integer) model.getValueAt(modelRow, 1);

        updatePanel.loadActor(actorId);
        tabs.setSelectedIndex(2);
    }

    private void openSelectedActorInDeleteTab() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione um actor na tabela primeiro.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int actorId = (Integer) model.getValueAt(modelRow, 1); // coluna ID no modelo

        deletePanel.loadActor(actorId);
        tabs.setSelectedIndex(3); // "Eliminar"
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

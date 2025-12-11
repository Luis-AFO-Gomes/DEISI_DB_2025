package pt.ulusofona.aed.deisimdb;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DeleteActorPanel extends JPanel {

    private final JComboBox<ActorItem> cbActor = new JComboBox<>();
    private final JLabel nameLabel = new JLabel("-");
    private final JLabel genderLabel = new JLabel("-");
    private final JLabel movieLabel = new JLabel("-");

    private final JButton btnDelete = new JButton("Eliminar");
    private final JButton btnReload = new JButton("Recarregar lista");
    private final JLabel status = new JLabel(" ");

    private final Dao dao = new Dao();
    private final Runnable onActorDeleted;

    private Actor currentActor; // actor actualmente carregado

    public DeleteActorPanel(Runnable onActorDeleted) {
        this.onActorDeleted = onActorDeleted;

        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.NORTH);
        add(buildFooter(), BorderLayout.SOUTH);

        cbActor.addActionListener(e -> loadSelectedActor());
        btnReload.addActionListener(e -> loadActorList(null));
        btnDelete.addActionListener(e -> deleteCurrentActor());

        loadActorList(null);
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Actor ID (combo)
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Actor ID:"), c);
        c.gridx = 1;
        p.add(cbActor, c);

        // Nome
        row++;
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Nome:"), c);
        c.gridx = 1;
        p.add(nameLabel, c);

        // Género
        row++;
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Género:"), c);
        c.gridx = 1;
        p.add(genderLabel, c);

        // Movie ID
        row++;
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Movie ID:"), c);
        c.gridx = 1;
        p.add(movieLabel, c);

        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(btnDelete);
        left.add(btnReload);

        p.add(left, BorderLayout.WEST);
        p.add(status, BorderLayout.CENTER);

        return p;
    }

    /** Carrega a lista de actores no combo; se selectId != null, selecciona esse. */
    private void loadActorList(Integer selectId) {
        cbActor.removeAllItems();
        currentActor = null;

        String sql = "SELECT actorID,actorName FROM dbo.tf_getActiveActors() ORDER BY actorID DESC;";

        try (Connection conn = dao.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ActorItem toSelect = null;

            while (rs.next()) {
                int id = rs.getInt("actorID");
                String name = rs.getString("actorName");
                ActorItem item = new ActorItem(id, name);
                cbActor.addItem(item);

                if (selectId != null && id == selectId) {
                    toSelect = item;
                }
            }

            if (toSelect != null) {
                cbActor.setSelectedItem(toSelect);
            } else if (cbActor.getItemCount() > 0) {
                cbActor.setSelectedIndex(0);
            } else {
                clearFields();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar lista de actores: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        loadSelectedActor();
    }

    /** Carrega os dados do actor seleccionado para as labels. */
    private void loadSelectedActor() {
        ActorItem item = (ActorItem) cbActor.getSelectedItem();
        if (item == null) {
            clearFields();
            return;
        }

        // Usa o construtor Actor(int actorId) que lê da BD
        currentActor = new Actor(item.id());

        nameLabel.setText(currentActor.getName());
        genderLabel.setText(String.valueOf(currentActor.getGender()));
        Integer movieId = currentActor.getMovieId();
        movieLabel.setText(movieId != null ? movieId.toString() : "(null)");

        status.setText("Actor " + currentActor.getActorId() + " carregado.");
    }

    /** Chamado de fora para abrir este tab já com o actor seleccionado. */
    public void loadActor(int actorId) {
        loadActorList(actorId);
    }

    private void deleteCurrentActor() {
        if (currentActor == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum actor seleccionado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String msg = "Tem a certeza que pretende eliminar o actor:\n" +
                     "ID: " + currentActor.getActorId() + "\n" +
                     "Nome: " + currentActor.getName() + "\n\n" +
                     "Esta operação não pode ser anulada.";

        int choice = JOptionPane.showConfirmDialog(
                this,
                msg,
                "Confirmar eliminação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            status.setText("Eliminação cancelada.");
            return;
        }

        // Elimina na BD
        currentActor.deleteDB();

        status.setText("Actor eliminado.");

        if (onActorDeleted != null) {
            onActorDeleted.run(); // refrescar tabela na tab de listagem
        }

        // Recarrega lista (sem selecção específica)
        loadActorList(null);
    }

    private void clearFields() {
        nameLabel.setText("-");
        genderLabel.setText("-");
        movieLabel.setText("-");
        status.setText(" ");
    }

    /** Item do combo: mostra "id - nome" mas guarda id e name. */
    private record ActorItem(int id, String name) {
        @Override
        public String toString() {
            return id + " - " + name;
        }
    }
}


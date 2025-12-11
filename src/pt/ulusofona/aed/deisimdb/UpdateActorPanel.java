package pt.ulusofona.aed.deisimdb;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UpdateActorPanel extends JPanel {

    private final JComboBox<ActorItem> cbActor = new JComboBox<>();
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> genderCombo =
            new JComboBox<>(new String[]{"F", "M"});
    private final JTextField movieIdField = new JTextField(10);

    private final JButton btnSave = new JButton("Atualizar");
    private final JButton btnReload = new JButton("Recarregar lista");
    private final JLabel status = new JLabel(" ");

    private final Dao dao = new Dao();
    private final Runnable onActorUpdated;

    public UpdateActorPanel(Runnable onActorUpdated) {
        this.onActorUpdated = onActorUpdated;

        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.NORTH);
        add(buildFooter(), BorderLayout.SOUTH);

        cbActor.addActionListener(e -> loadSelectedActor());
        btnSave.addActionListener(e -> save());
        btnReload.addActionListener(e -> loadActorList(null));

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
        p.add(nameField, c);

        // Género
        row++;
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Género:"), c);
        c.gridx = 1;
        p.add(genderCombo, c);

        // Movie ID
        row++;
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Movie ID (opcional):"), c);
        c.gridx = 1;
        p.add(movieIdField, c);

        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(btnSave);
        left.add(btnReload);

        p.add(left, BorderLayout.WEST);
        p.add(status, BorderLayout.CENTER);

        return p;
    }

    /** Carrega a lista de actores para o combo; se selectId != null, selecciona esse ID. */
    private void loadActorList(Integer selectId) {
        cbActor.removeAllItems();

        String sql = "SELECT actorID, actorName FROM dbo.actors ORDER BY actorID";

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

    /** Carrega dados do actor seleccionado no combo para os campos. */
    private void loadSelectedActor() {
        ActorItem item = (ActorItem) cbActor.getSelectedItem();
        if (item == null) {
            clearFields();
            return;
        }

        // Usa o teu construtor Actor(int actorId) que lê da BD
        Actor actor = new Actor(item.id());

        nameField.setText(actor.getName());
        genderCombo.setSelectedItem(String.valueOf(actor.getGender()));
        Integer movieId = actor.getMovieId();
        movieIdField.setText(movieId != null ? movieId.toString() : "");

        status.setText("Actor " + actor.getActorId() + " carregado.");
    }

    /** Chamado de fora (da lista) para abrir o tab já no actor certo. */
    public void loadActor(int actorId) {
        loadActorList(actorId); // repovoa e selecciona esse id
    }

    private void save() {
        ActorItem item = (ActorItem) cbActor.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum actor seleccionado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id = item.id();
        String name = nameField.getText().trim();
        String genderTxt = (String) genderCombo.getSelectedItem();
        String movieTxt = movieIdField.getText().trim();

        if (name.isEmpty() || genderTxt == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome e Género são obrigatórios.",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Integer movieId = null;
        if (!movieTxt.isEmpty()) {
            try {
                movieId = Integer.parseInt(movieTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Movie ID tem de ser um inteiro (ou vazio).",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        char gender = genderTxt.charAt(0);

        // Carrega actor actual, actualiza e faz updateDB()
        Actor actor = new Actor(id);
        actor.setActorName(name);
        actor.setActorGender(gender);
        actor.setMovieId(movieId);

        actor.updateDB();

        status.setText("Actor atualizado.");

        if (onActorUpdated != null) {
            onActorUpdated.run(); // para fazer refresh na tabela
        }

        // Recarrega lista para actualizar nome no combo e manter ID seleccionado
        loadActorList(id);
    }

    private void clearFields() {
        nameField.setText("");
        genderCombo.setSelectedIndex(0);
        movieIdField.setText("");
        status.setText(" ");
    }

    /** Item do combo: mostra "id - nome", guarda id e nome. */
    private record ActorItem(int id, String name) {
        @Override
        public String toString() {
            return id + " - " + name;
        }
    }
}


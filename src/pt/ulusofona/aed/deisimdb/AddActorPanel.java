package pt.ulusofona.aed.deisimdb;

import javax.swing.*;
import java.awt.*;

public class AddActorPanel extends JPanel {
// Campos do formulário
    private final JTextField idField = new JTextField(10);
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> genderCombo =
            new JComboBox<>(new String[]{"F", "M"});
    private final JTextField movieIdField = new JTextField(10);

    private final JButton saveBtn = new JButton("Inserir Actor"); // Botão para inserir actor
    private final JButton clearBtn = new JButton("Limpar Formulário"); // Botão para limpar formulário
    private final JLabel status = new JLabel("Preencher os campos e clicar em 'Inserir Actor'");

    private final Runnable onActorAdded;

    public AddActorPanel(Runnable onActorAdded) {
        this.onActorAdded = onActorAdded;

        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.NORTH);
        add(buildFooter(), BorderLayout.SOUTH);

// Adicionar comportamento aos botões
        saveBtn.addActionListener(e -> onSave());
        clearBtn.addActionListener(e -> clearFields());
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;

        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Actor ID:"), c);
        c.gridx = 1;
        p.add(idField, c);

        row++;
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Nome:"), c);
        c.gridx = 1;
        p.add(nameField, c);

        row++;
        c.gridx = 0; c.gridy = row;
        p.add(new JLabel("Género:"), c);
        c.gridx = 1;
        p.add(genderCombo, c);

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
        left.add(saveBtn);
        left.add(clearBtn);

        p.add(left, BorderLayout.WEST);
        p.add(status, BorderLayout.CENTER);
        return p;
    }

    private void onSave() {
        String idTxt = idField.getText().trim();
        String name = nameField.getText().trim();
        String genderTxt = (String) genderCombo.getSelectedItem();
        String movieTxt = movieIdField.getText().trim();

        if (idTxt.isEmpty() || name.isEmpty() || genderTxt == null) {
            showError("Preencha pelo menos Actor ID, Nome e Género.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idTxt);
        } catch (NumberFormatException ex) {
            showError("Actor ID tem de ser um inteiro.");
            return;
        }

        Integer movieIdParsed = null;
        if (!movieTxt.isEmpty()) {
            try {
                movieIdParsed = Integer.parseInt(movieTxt);
            } catch (NumberFormatException ex) {
                showError("Movie ID tem de ser um inteiro (ou vazio).");
                return;
            }
        }

        char gender = genderTxt.charAt(0);

        final Actor a = new Actor(id, name, gender,movieIdParsed);

        saveBtn.setEnabled(false);
        status.setText("A inserir...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // assumes you have the 3-arg constructor + setter
//                Actor a = new Actor(id, name, gender,movieId);
//                a.setMovieId(movieId); // Integer
                a.insertDB();
                return null;
            }

            @Override
            protected void done() {
                saveBtn.setEnabled(true);
                status.setText("Actor inserido.");

                clearFields();

                if (onActorAdded != null) {
                    onActorAdded.run();
                }
            }
        };

        worker.execute();
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        movieIdField.setText("");
        genderCombo.setSelectedIndex(0);
    }

    private void showError(String msg) {
        status.setText(" ");
        JOptionPane.showMessageDialog(this, msg, "Validação", JOptionPane.WARNING_MESSAGE);
    }
}


package pt.ulusofona.aed.deisimdb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Actor {

    private int actorId;
    private String actorName;
    private char actorGender;
    private Integer movieId = null;
    private TipoEntidade tipo;
    private Dao dao;

    private String generoLongo;


    public Actor(int actorId, String actorName, char actorGender, Integer movieId) {
        this.actorId = actorId;
        this.actorName = actorName;
        this.actorGender = actorGender;
        this.movieId = movieId;

        this.tipo = TipoEntidade.ATOR;

        dao = new Dao();

        if(actorGender=='F'){
            generoLongo= "Feminino";
        }else{
            generoLongo= "Masculino";
        }
    }

    public Actor(int actorId) {
        this.actorId = actorId;

        this.tipo = TipoEntidade.ATOR;

        dao = new Dao();
        ResultSet resultSet = null;
        
        String selectSql = "SELECT actorName,actorGender,movieID FROM dbo.actors WHERE actorID = ?;";

        try (Connection connection = dao.getConnection();
            PreparedStatement statement = connection.prepareStatement(selectSql);) {

            statement.setInt(1, this.actorId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.actorName = resultSet.getString("actorName");
                this.actorGender = resultSet.getString("actorGender").charAt(0);
//                this.movieId = resultSet.getInt("movieID") ;
                this.movieId = resultSet.wasNull() ? null : resultSet.getInt("movieID");
            }

            if(actorGender=='F'){
                generoLongo= "Feminino";
            }else{
                generoLongo= "Masculino";
        }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getActorId() {
        return actorId;
    }
    public void setActorName(String name) {
        this.actorName = name;
    }
    public String getName() {
        return actorName;
    }

    public void setActorGender(char gender) {
        this.actorGender = gender;
    }
    public char getGender() {
        return actorGender;
    }

    public String getGeneroLongo() {
        return generoLongo;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
    public Integer getMovieId() {
        return movieId;
    }


    public TipoEntidade getTipo() {
        return tipo;
    }

    public void insertDB() {
/************************************************************************************
 * Na versão implementada, os actores só são apresentados se estiverem activos, ou  *
 * seja, [actors].[active] = 1                                                      *
 * Isto significa que o actor pode existir mas o utilizador não o vê, levando a uma *
 * tentativa de inserção que resultaria em erro por duplicação de chave primária.   *
 * Para evitar este erro, a inserção é feita através de uma stored procedure que    *
 * verifica se o actor existe e está inactivo, activando-o em vez de o inserir, ou  *
 * inserindo-o se não existir.                                                      *
 *                                                                                  *       
 * É boa prática encapsular a lógica de negócio na base de dados, utilizando SP em  *
 * vez de código aplicacional. Esta abordagem evita erros e permite lógica mais     *
 * robusta no tratamento de dados e garante a integridade dos mesmos.               *
 ************************************************************************************/              
        try (Connection connection = dao.getConnection();) {
            String sql = "{CALL dbo.sp_insertActor(?, ? ,? ,?)}";
            CallableStatement stmt = connection.prepareCall(sql);
            // Set input parameters
            stmt.setInt(1, this.actorId);
            stmt.setString(2, this.actorName);
            stmt.setString(3, String.valueOf(this.actorGender));
//            stmt.setInt(4, this.movieId);
            if (this.movieId == null){
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, this.movieId);
            }

        // Execute the stored procedure
            var result = stmt.execute();

            if (result) {
                System.out.println("Actor " + this.actorId + ", " + this.actorName + " foi inserido.");
            }
        } catch(SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

/************************************************************************************
 * Para complemento ao exemplo, fica aqui o código de inserção direta, por INSERT   *
 * que na versão implementada não é utilizado por razões explicadas acima.          *
 * A inserção directa pode ser utilizada noutras tabelas onde não seja necessária   *
 * lógica de negócio mais complexa ou não existam riscos para integridade dos dados *
 ************************************************************************************/
/*
    public void insertDB() {        
        var sql = "INSERT INTO dbo.actors (actorID, actorName, actorGender, movieID) VALUES (?,?,?,?);";
        try (Connection connection = dao.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            // Bind values to parameters
            statement.setInt(1, this.actorId);
            statement.setString(2, this.actorName);
            statement.setString(3, String.valueOf(this.actorGender));
            statement.setInt(4, this.movieId);

            // Execute the query
            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

    }
*/

    public void updateDB() {        
        var updateSQL = "UPDATE dbo.actors SET actorName = ?, actorGender = ?, movieID = ? WHERE actorID = ?;";
        try (Connection connection = dao.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            // Bind values to parameters
            statement.setString(1, this.actorName);
            statement.setString(2, String.valueOf(this.actorGender));
            if (this.movieId != null) {
                statement.setInt(3, this.movieId);
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            statement.setInt(4, this.actorId);

            // Execute the query
            int rowsUpdated = statement.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Actor " + this.actorId + ", " + this.actorName + " foi actualizado.");
            } else {
                System.out.println("Não existe actor com ID: " + this.actorId);
            }

            if(actorGender=='F'){
                generoLongo= "Feminino";
            }else{
                generoLongo= "Masculino";
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteDB() {
/************************************************************************************
 * Na versão implementada, a tabela de actores tem trigger INSTEAD OF DELETE que    *
 * vai alterar o estado do actor para inactivo, mas sem o eliminar                  *
 * Esta acção é transparente para o utilizador, mantendo-se a instrução de DELETE   *
 * sem alteração visível para o utilizador.                                         *
 ************************************************************************************/
        var updateSQL = "DELETE FROM dbo.actors WHERE actorID = ?;";
        try (Connection connection = dao.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            // Bind values to parameters
            statement.setInt(1, this.actorId);

            // Execute the query
            int rowsDeleted = statement.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Actor " + this.actorId + ", " + this.actorName + " foi eliminado.");
            } else {
                System.out.println("Não existe actor com ID: " + this.actorId);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return  actorId + " | " + actorName + " | " + generoLongo + " | " + movieId;
    }


}

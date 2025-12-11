package pt.ulusofona.aed.deisimdb;

import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

public class Main {
    private static ArrayList<Actor> atores = new ArrayList<>();

    //---------------------------------------------------------------------------------------------------------------------
    //public static ArrayList getObjects(TipoEntidade entidade) {
//
        //switch (entidade) {
          //  case ATOR -> {
            //    return atores;
//            }
//        }
//        return new ArrayList<>();
//    }

    public static String help(){
        return
                """     
                        Comandos disponíveis:
                        * COUNT_MOVIES_MONTH_YEAR <month> <year>
                        * COUNT_MOVIES_DIRECTOR <full-name>
                        * COUNT_ACTORS_IN_2_YEARS <year-1> <year-2>
                        * COUNT_MOVIES_BETWEEN_YEARS_WITH_N_ACTORS <year-start> <year-end> <min> <max>
                        * GET_MOVIES_ACTOR_YEAR <year> <full-name>
                        * GET_MOVIES_WITH_ACTOR_CONTAINING <name>
                        * GET_TOP_4_YEARS_WITH_MOVIES_CONTAINING <search-string>
                        * GET_ACTORS_BY_DIRECTOR <num> <full-name>
                        * TOP_MONTH_MOVIE_COUNT <year>
                        * TOP_VOTED_ACTORS <num> <year>
                        * TOP_MOVIES_WITH_MORE_GENDER <num> <year> <gender>
                        * TOP_MOVIES_WITH_GENDER_BIAS <num> <year>
                        * TOP_6_DIRECTORS_WITHIN_FAMILY <year-start> <year-end>   
                        LISTAR_ACTORES
                        INSERT_ACTOR <id>;<name>;<gender>;<movie-id>
                        UPDATE_ACTOR <id>;<name>;<gender>;<movie-id>
                        APAGAR_ACTOR <id>
                        * INSERT_DIRECTOR <id>;<name>;<movie-id>
                        * DISTANCE_BETWEEN_ACTORS <actor-1>,<actor-2>
                        HELP
                        QUIT
                        
                        * Comandos por implementar""";
    }
 
    public static String larguraFixa(String input, int length, char fillChar) {
    if (input == null) input = "";
    StringBuilder sb = new StringBuilder(input);
    while (sb.length() < length) {
        sb.append(fillChar);
    }
    return sb.toString();
    }

    public static boolean actorExists(int actorID){
/************************************************************************************
 * Exemplos de chamadas a funções definidas em base de dados                        *
 * A função ufn_actorIdsOcupados devolve TRUE se o actorID existir na tabela        *
 ************************************************************************************/
//  Criar o objecto para ligar à base de dados
        Dao dao = new Dao();
//  Definição da instrução para consulta à base de dados
//  Note-se que a chamada é feita com sintaxe Java - call - e não com a sintaxe T-SQL - SELECT
//  Esta sintaxe é complementada com o uso da classe CallableStatement
//  Para reter o valor devolvido pela função, indica-se um parâmetro de saída (OUT) antes da chamada à função - ? = call ...
        String selectSql = "{ ? = call dbo.ufn_actorIdsOcupados(?) }";
        try (Connection connection = dao.getConnection();
                CallableStatement statement = connection.prepareCall(selectSql)) {

//  Resgisto re parametros para chamada da função                    
            statement.registerOutParameter(1, Types.BIT);
            statement.setInt(2, actorID);

// A chamada à função é feita apenas neste momento, com o método execute()
            statement.execute();

            return statement.getBoolean(1);
        }
//  Tratamento de erros e excepções na leitura da base de dados
        catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
 //---------------------------------------------------------------------------------------------------------------------
    public static String countMoviesMonthYear(int mes, int ano){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String countMoviesDirector(String nome){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String countActorsIn2Years(int ano1, int ano2) {
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String countMoviesBetweenYearsWitnNActors(int anoI, int anoF, int minAtor, int maxAtor){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String getMoviesActorYear(int ano, String nome){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String getMoviesWithActorContaining(String nome){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String getTop4YearsWithMoviesContaining(String procura){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String getActorsByDirector(int num, String nome){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String topMonthMovieCount(int ano){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String topVotedActors(int num, int ano){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String topMoviesWithMoreGender(int num, int ano, char gen){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String topMoviesWithGenderBias(int num, int ano){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String top6DirectorsWithinFamily(int anoI, int anoF){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }

    public static String listarActores(){
//  Criar o objecto para ligar à base de dados
        Dao dao = new Dao();
//  Ler dados com um SELECT simples
        System.out.println();
        System.out.println("Listar actores activos-------------------\n");
        System.out.println(larguraFixa("-",55,'-'));
        System.out.println(("| ") + larguraFixa("Nome",29,' ') 
                        + "| "  + larguraFixa("ID",9,' ') 
                        + "| "  + larguraFixa("Género",10,' ') + "|");
        System.out.println(larguraFixa("-",55,'-'));
        ResultSet resultSet = null;

//  Criar a ligação à base de dados para executar o SELECT        
        try (Connection connection = dao.getConnection();
                Statement statement = connection.createStatement();) {

//  Definir e executar a instrução de leitura (SELECT)
//  A leitura é efectuada por função definida em base de dados de modo a filtrar actores activos
            String selectSql = "SELECT actorID,actorName,actorGender FROM dbo.tf_getActiveActors() ORDER BY actorID DESC;";
            resultSet = statement.executeQuery(selectSql);

//  Apresentar resultados em consola
            while (resultSet.next()) {
                
        System.out.println("| " + larguraFixa(resultSet.getString(2),29,' ') 
                        + "| "  + larguraFixa(resultSet.getString(1),9,' ') 
                        + "| "  + larguraFixa(resultSet.getString(3).equals("F") ? "Feminino" : "Masculino",10,' ') + "|");
            }
            System.out.println(larguraFixa("-",55,'-'));
        }
//  Tratamento de erros e excepções na leitura da base de dados
        catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return "Listagem concluida";
    }
    
    public static String insertActor(int id, String nome, char gender, int movieId){
        if(actorExists(id)){
//          carregar actor se já existir, confirmando primeiro com o utilizador            
            System.out.println("Actor " + id + " já existe, vai ser carregado da base de dados.");
            Actor actor = new Actor(id);

            System.out.println("actor: " + actor.toString());
            return "Actor carregado";
        } else {
            System.out.println("Actor " + id + " não existe, vai ser inserido na base de dados.");

            if(!(gender=='M' || gender=='F') ){
                return "Erro";
            }
            if(nome.trim().isEmpty()){
                return "Erro";
            }

            Actor actor = new Actor(id,nome,gender,movieId);
            actor.insertDB();

            System.out.println("actor: " + actor.toString());
            return "Actor inserido";
        }
    }

    public static String updateActor(int id, String nome, char gender, Integer movieId){

        if(actorExists(id)){
//          carregar actor se já existir, confirmando primeiro com o utilizador            
            System.out.println("Actor " + id + " existe, vai ser actualizado na base de dados.");
            Actor actor = new Actor(id);
            actor.setActorName(nome);
            actor.setActorGender(gender);
            actor.setMovieId(movieId);  

            actor.updateDB();

            System.out.println("actor: " + actor.toString());
            return "Actor actualizado";
        } else {
            System.out.println("Actor " + id + " não existe,  não serão efectuadas alterações.");
            return "sem resultados";
        }
    }    

    public static String deleteActor(int id){
        if(actorExists(id)){
//          carregar actor se já existir, confirmando primeiro com o utilizador            
            Actor actor = new Actor(id);
            actor.deleteDB();
            actor=null;
            return "Actor %d eliminado".formatted(id);
        } else {
            return "Actor %d não existe".formatted(id);
        }
    }

    public static String insertDirector(int id, String nome, int movieId){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }
    public static String distanceBetweenActors(String ator1, String ator2){
        return ("Resultado de função/procedimento escrito em SQL (por implementar)");
    }

    public static Result execute(String command){
        String[] partido = command.split(" ");
        String result;

        switch (partido[0]) {
            case "COUNT_MOVIES_MONTH_YEAR" -> {
                int mes = Integer.parseInt(partido[1]);
                int ano = Integer.parseInt(partido[2]);
                result = countMoviesMonthYear(mes, ano);
                return new Result(true, null, result);
            }

            case "COUNT_MOVIES_DIRECTOR" -> {
                StringBuilder nome=new StringBuilder();
                for(int i=1;i< partido.length;i++){
                    if(i>1){nome.append(" ");}
                    nome.append(partido[i]);
                }

                result = countMoviesDirector(nome.toString());
                return new Result(true, null, result);
            }

            case "COUNT_ACTORS_IN_2_YEARS" -> {
                int ano1 = Integer.parseInt(partido[1]);
                int ano2 = Integer.parseInt(partido[2]);
                result = countActorsIn2Years(ano1, ano2);
                return new Result(true, null, result);
            }

            case "COUNT_MOVIES_BETWEEN_YEARS_WITH_N_ACTORS" -> {
                int anoI = Integer.parseInt(partido[1]);
                int anoF = Integer.parseInt(partido[2]);
                int minAtor = Integer.parseInt(partido[3]);
                int maxAtor = Integer.parseInt(partido[4]);
                result = countMoviesBetweenYearsWitnNActors(anoI, anoF, minAtor, maxAtor);
                return new Result(true, null, result);
            }

            case "GET_MOVIES_ACTOR_YEAR" -> {
                int ano = Integer.parseInt(partido[1]);
                StringBuilder nome=new StringBuilder();
                for(int i=2;i< partido.length;i++){
                    if(i>2){nome.append(" ");}
                    nome.append(partido[i]);
                }
                result = getMoviesActorYear(ano, nome.toString());
                return new Result(true, null, result);
            }

            case "GET_MOVIES_WITH_ACTOR_CONTAINING" -> {
                StringBuilder nome=new StringBuilder();
                for(int i=1;i< partido.length;i++){
                    if(i>1){nome.append(" ");}
                    nome.append(partido[i]);
                }
                result = getMoviesWithActorContaining(nome.toString());
                return new Result(true, null, result);
            }

            case "GET_TOP_4_YEARS_WITH_MOVIES_CONTAINING" -> {
                String procura = partido[1];
                result = getTop4YearsWithMoviesContaining(procura);
                return new Result(true, null, result);
            }

            case "GET_ACTORS_BY_DIRECTOR" -> {
                int num = Integer.parseInt(partido[1]);
                StringBuilder nome=new StringBuilder();
                for(int i=2;i< partido.length;i++){
                    if(i>2){nome.append(" ");}
                    nome.append(partido[i]);
                }
                result = getActorsByDirector(num, nome.toString());
                return new Result(true, null, result);
            }

            case "TOP_MONTH_MOVIE_COUNT" -> {
                int ano = Integer.parseInt(partido[1]);
                result = topMonthMovieCount(ano);
                return new Result(true, null, result);
            }

            case "TOP_VOTED_ACTORS" -> {
                int num = Integer.parseInt(partido[1]);
                int ano = Integer.parseInt(partido[2]);
                result = topVotedActors(num, ano);
                return new Result(true, null, result);
            }

            case "TOP_MOVIES_WITH_MORE_GENDER" -> {
                int num = Integer.parseInt(partido[1]);
                int ano = Integer.parseInt(partido[2]);
                char c = partido[3].charAt(0);
                result = topMoviesWithMoreGender(num, ano, c);
                return new Result(true, null, result);
            }

            case "TOP_MOVIES_WITH_GENDER_BIAS" -> {
                int num = Integer.parseInt(partido[1]);
                int ano = Integer.parseInt(partido[2]);
                result = topMoviesWithGenderBias(num, ano);
                return new Result(true, null, result);
            }

            case "TOP_6_DIRECTORS_WITHIN_FAMILY" -> {
                int anoI = Integer.parseInt(partido[1]);
                int anoF = Integer.parseInt(partido[2]);
                result = top6DirectorsWithinFamily(anoI, anoF);
                return new Result(true, null, result);
            }

            case "LISTAR_ACTORES" -> {
                result = listarActores();
                return new Result(true, null, result);
            }

            case "INSERT_ACTOR" -> {
                String parte = command.substring(13);
                String[] partido2 = parte.split(";");

                int id = Integer.parseInt(partido2[0]);
                String nome = partido2[1];
                char gender = partido2[2].charAt(0);
                int movieId = Integer.parseInt(partido2[3]);


                result = insertActor(id, nome, gender, movieId);
                return new Result(true, null, result);
            }

            case "UPDATE_ACTOR" -> {
                String parte = command.substring(13);
                String[] partido2 = parte.split(";");

                if (partido2.length<3) {
                    return new Result(false, "Parâmetros insuficientes para UPDATE_ACTOR", null);
                } else {
                    int id = Integer.parseInt(partido2[0]);
                    String nome = partido2[1];
                    char gender = partido2[2].charAt(0);
                    Integer movieId;
                    if (partido2.length == 4) {
                        movieId = partido2[3].isEmpty() ? null : Integer.parseInt(partido2[3]);
                    } else {
                        movieId = null; 
                    }
                    result = updateActor(id, nome, gender, movieId);
                }

                return new Result(true, null, result);
            }

            case "APAGAR_ACTOR" -> {
                String parte = command.substring(13);
                String[] partido2 = parte.split(";");

                int id = Integer.parseInt(partido2[0]);

                result = deleteActor(id);
                return new Result(true, null, result);
            }

            case "INSERT_DIRECTOR" -> {
                String parte = command.substring(16);
                String[] partido2 = parte.split(";");


                int id = Integer.parseInt(partido2[0]);
                String nome = partido2[1];
                int movieId = Integer.parseInt(partido2[2]);
                result = insertDirector(id, nome, movieId);
                return new Result(true, null, result);
            }

            case "DISTANCE_BETWEEN_ACTORS" -> {
                String[] partido2 = partido[2].split(",");
                String ator1 = partido[1] + " " + partido2[0];
                String ator2 = partido2[1] + " " + partido[3];//gambiarra ahah

                result = distanceBetweenActors(ator1, ator2);
                return new Result(true, null, result);

            }

            case "HELP" -> {
                result = help();
                return new Result(true, null, result);
            }

            default -> {
                return new Result(true, "Comando invalido", null);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        System.out.println("-----------------------------------------\n\n");
// Código original        
        long start= System.currentTimeMillis();
        long end= System.currentTimeMillis();

        Result result = execute("HELP");
        System.out.println(result.result);

        Scanner in = new Scanner(System.in);

        String line;
        do {
            System.out.print("> ");
            line = in.nextLine(); // Read input at the start of the loop

            if (line != null && !line.equals("QUIT")) {
                start= System.currentTimeMillis();
                result = execute(line);
                end= System.currentTimeMillis();


                if (!result.success) {
                    System.out.println("Erro: " + result.error);
                } else {
                    System.out.println(result.result);
                    System.out.println("(Demorou: " + (end-start) +" ms)");
                }
            }
        } while (line != null && !line.equals("QUIT"));

    }
}
# File Tree
<pre>
│   README.md
│
├───lib
│       mssql-jdbc-13.2.1.jre11-javadoc.jar
│       mssql-jdbc-13.2.1.jre11-sources.jar
│       mssql-jdbc-13.2.1.jre11.jar
│
├───sql
│       DDL.sql
│       deisIMDB.sql
│
├───src
│   └───pt
│       └───ulusofona
│           └───aed
│               └───deisimdb
│                       Actor.java
│                       Dao.java
│                       Main.java
│                       Result.java
│                       TipoEntidade.java
│
└───test-files
    ├───demoFiles
    │       actors.csv
    │       directors.csv
    │       genres.csv
    │       genres_movies.csv
    │       movies.csv
    │       movie_votes.csv
    │
    └───realFiles
            actors.csv
            directors.csv
            genres.csv
            genres_movies.csv
            movies.csv
            movie_votes.csv
Onde:
lib: Contém as bibiotecas necessárias para executar o exemplo (e.g. JDBC de SQL)
sql: scritps de SQL para criar o ambiente de testes
    - DDL.sql: Scripts para criar a estrutura de dados necessária à importação de dados
            scripts para importação de dados
            NOTA: 
            os scripts são executados em linha de comandos (Windows-> PowerShell; MacOS-> Terminal; Linux: Cli/Bash), não em SQL.
            Mais informações abaixo
    - deisIMDB.sql: Scripts para criar os objectos especificos chamados pelo código de exemplo      
            disponibilizado
src\pt\ulusofona\aed\deisimdb: código fonte da solução
test-files: ficheiros com dados de trabalho

# Preparação do ambiente
A organização de ficheiros segue uma estrutura de projecto trabalhado em VSCode, mas pode ser utilizado em qualquer IDE de JAVA desde que se apliquem todas as regras adequadas ao contexto de trabalho (e.g. configuração de debug e compilação, etc.)
O exemplo está preparado para executar sobre uma base de dados criada de raiz - deisIMDB - no servidor utilizado nas aulas práticas. A dase de dados é criada em DDL.sql

Para preparar o ambiente, executar as seguites acções:
1.  Criar um novo projecto de JAVA em VSCode
    Caso se utilize outro IDE, é necessário conseguir executar comandos SQL sobre um servidor MSSQL
2.  Importar os ficheiros para o projecto de modo que a estrutura de pasta fique com a configuração indicada em #File Tree
3.  Abrir o ficheiro DDL.sql
4.  Abrir ligaçao a servidor SQL com credenciais adequadas
    Caso o ambiente docker disponibilizado para as aulas práticas não tenha sido alterado, utilizar as indicações de configuração de ambiente de trabalho das aulas práticas existente em Moodle (https://moodle.ensinolusofona.pt/mod/book/view.php?id=20951)
    Caso se esteja a utiliza outro servidor de dados, utilizar as credenciais apropriadas
5.  Executar os comandos do script
    Os comandos podem ser executados isoladamente ou em conjunto
    No final, verificar se todas as tabelas foram criadas consultando o INFORMATION_SCHEMA

# Código fonte
Ficheiros
- Actor.java <br>
  Classe de definição de actor
  Contém atributos de actor e métodos de exemplificação de ligação a base de dados (CRUD)
  No âmbito do exemplo, os atributos mapeiam directamente os dados obtidos da importação dos ficheiros CSV, acrescendo-se apenas um atributo de estado necessário para implementação do comportamento de "undelete" (desactivar registos em vez de os eliminar)
  Explicação em detalhe mais abaixo
- Dao.java <br>
  Classe com especificações para ligar a base de dados
  Para simplificação do código e segurança, utiliza-se esta classe para centralizar o código próprio para ligaçao a base de dados, facilitando a leitura e compreenção do processo.
  A cada método que acede à base de dados, é instanciando um objecto desta classe, evitando a repetição de código e riscos de erros sintáticos
- Main.java <br>
  Classe principal de aplicações estáticas de Java
  Faz a interface de utilizador e executa as funções de calculo 
Result.java <br>
- Classe para apresentação de resultados de cada funcionalidade da aplicação
TipoEntidade.java <br>
- Enumerado com as classes utilizaveis na aplicação

# Classe de exemplo: Actor
A classe utiliza dois construtores

# Classe de ligação a base de dados: DAO
A classe tem com atributos os elementos necessários para realizar a ligaçao a uma base de dados MS-SQL: 
    sqlserver: endereço do servidor
    databaseName: nome da base de dados para a ligação
    user: nome de utilizador
    password: palavra-passe de [user]
    encrypt: definição de encriptação da ligação ([default]true/false)
    trustServerCertificate: Confiabilidade do certificado de utilizador

O método getConnection() devolve um objecto de ligaçao a base de dados com as caracteristicas definidas na classe

Pode ser criada um construtor em overload caso se pretenda que o objecto seja criado com caracteristicas diferentes dos iniciais

# Utilização de exemplos
O exemplo disponibilizado inclui todas as funções solicitadas no projecto base de AED, mas também algumas que se pode antecipar desde já que venham a ser desenvolvidas na cadeira de Base de Dados.
Todas as funcionalidades são listadas em menus de opções, carregado no lançamento da aplicação ou sempre que se seleccionar a opção "Help", mas apenas algumas estão disponíveis, as necessárias para exemplificação de acesso a bases de dados utilizando Java.
Assim, as opções com o prefixo '*' não estão desenvolvidas

Para os melhores resultados do exemplo, os dados já devem estar carregados para a base de dados, preferencialmente apenas os de demonstração, para evitar que a experiência 'danifique' os dados reais a utilizar no projecto

O modo mais eficaz para verificar o funcionamento será realizar a seguite chamada de funcionalidades:
1. Listar actores (LISTAR_ACTORES)
    A lista deve conter exactamente os actores importados do ficheiro CSV
2. Inserir dois actores (INSERT_ACTOR <id>;<name>;<gender>;<movie-id>) 
   Um dos actore deve ter filme associado; o segundo não tem filme
   - Pode ser necessário alterar a tabela de actores para aceitar nulos na coluna de filmes
    A cada actor inserido, a aplicação irá confirmar a sua existência e listar os seus dados
    No actor duplicado será dada a indicação que já existe, os dados apresentados serão os existentes na base de dados e não o inseridos
3. Inserir actor duplicado (INSERT_ACTOR <id>;<name>;<gender>;<movie-id>)  
   - Sugestão: duplicar apenas o ID, os restantes dados devem ser diferentes
    A a aplicação irá confirmar que o actor já existe e listar os seus dados, os dados apresentados serão os existentes na base de dados e não o inseridos   
4. Listar actores (LISTAR_ACTORES)
    A lista deve conter os dois novos actores inseridos, sem alteraçoes no dulicado
5. Alterar um actor (UPDATE_ACTOR <id>;<name>;<gender>;<movie-id>)
   - Sugestão: alterar o actor da duplicaçao em 2
    A aplicaçao confirma existência e apresenta dados actualizado
6. Alterar um actor inexistente (UPDATE_ACTOR <id>;<name>;<gender>;<movie-id>)
    Indicação de actor inexistente
7. Eliminar os dois actores inseridos em 2 (APAGAR_ACTOR <id>) 
    A cada actor, a aplicaçao confirma a eliminação sem diferenças visiveis  
8. Listar actores (LISTAR_ACTORES)
    Nenhum do dois actores eliminados é apresentado
9. Numa sessão de SQL, consultar a tabela de actores (este teste não é realizado na aplicação JAVA)
    O actor com filme associado deve estar inactivo (isActive = 0) mas existe na tabela. 
    O actor sem filme associado não será listado
10. Inserir novamente o actor eliminado (INSERT_ACTOR <id>;<name>;<gender>;<movie-id>)     
    - Apenas interessa o actor com filme associado
    A aplicação informa que o actor já existe e que o irá carregar da base de dados, informação semelhante à apresentada numa inserção de raiz
11. Listar actores (LISTAR_ACTORES)
    O actor 'activado' é apresentado na lista
12. Numa sessão de SQL, consultar a tabela de actores (este teste não é realizado na aplicação JAVA)
    O actor 'inserido' estará activo (isActive = 1) na tabela.

# Código exemplo para deisIMDB 

## File Tree

<pre>
│   
│   DEISI_DB_2025.code-workspace
│   README.md
│   
├───images
│       image-1.png
│       image-2.png
│       image-3.png
│       image.png
│       output.png
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
    └───demoFiles
            actors.csv
            directors.csv
            genres.csv
            genres_movies.csv
            movies.csv
            movie_votes.csv
</pre>
Onde:<br>
**images**: Contém apenas as imagens de suporte a este ficheiro README<br>
**lib**: Contém as bibiotecas necessárias para executar o exemplo (e.g. JDBC de SQL)<br>
&emsp;&nbsp;&nbsp;&nbsp;Dependendo do IDE utilziado, pode ser necessário registar as bibliotecas<br>
**sql**: scritps de SQL para criar o ambiente de testes<br>
- **DDL.sql**: Scripts para criar a estrutura de dados necessária à importação de dados<br>
            &emsp;&emsp;&emsp;&emsp;Scripts para importação de dados<br>
            &emsp;&emsp;&emsp;&emsp;NOTA: <br>
            &emsp;&emsp;&emsp;&emsp;os scripts são executados em linha de comandos  (_powershell_ em windows, _terminal_ em MacOS ou _Cli/Bash_ em Linux) , não em SQL.<br>
            &emsp;&emsp;&emsp;&emsp;Mais informações abaixo na secção de [preparação do ambiente](#preparação-do-ambiente)<br>
- **deisIMDB.sql**: Scripts para criar os objectos especificos chamados pelo código de exemplo
            disponibilizado<br>

**src\pt\ulusofona\aed\deisimdb**: código fonte da solução<br>
**test-files**: ficheiros com dados de trabalho<br>

## Preparação do ambiente

A organização de ficheiros segue uma estrutura de projecto trabalhado em VSCode, incluindo ficheiro _workspace_ com configurações (***DEISI_DB_2025.code-workspace***). O exemplo pode ser utilizado qualquer IDE de JAVA desde que se apliquem todas as regras adequadas ao contexto de trabalho (e.g. configuração de debug e compilação, etc.).<br>
O exemplo está preparado para executar sobre uma base de dados criada de raiz - ***deisIMDB*** - no servidor utilizado nas aulas práticas. Também aqui, pode ser utilisado outro ambiente, com o cuidado de efectuar a adaptação necessária, nomeadamente no tocante a credenciais
A dase de dados é criada em DDL.sql, conforme se indica no ponto 5. abaixo

Para executar o código Java no VSCode, será necessário instalar as seguintes extensões:
- Extension Pack for Java, da Microsoft (instala 6 extensões)<br>
    <img src="images\image.png" alt="Microsoft Java Extension Pack" width="30%"><br>
- Language Support for Java (tm), Red Hat<br>
    <img src="images\image-1.png" alt="Red Hat Language Support for Java" width="35%"><br>
Opcionalmente, também se pode instalar os módulos<br>
- Java, Oracle Corporation<br>
    <img src="images\image-2.png" alt="Java, Oracle" width="35%"><br>
  Esta última não é necessária para executar o código mas melhora o intelissense<br>

Para preparar o ambiente, executar as seguites acções:
1. Criar um novo projecto de JAVA em VSCode<br>
    Caso se utilize outro IDE, é necessário garantir que se consegue executar comandos SQL sobre um servidor MSSQL
2. Importar os ficheiros para o projecto de modo que a estrutura de pasta fique com a configuração indicada na [File Tree](#file-tree)
3. Abrir o ficheiro DDL.sql
4. Abrir ligaçao a servidor SQL com credenciais adequadas
    Caso o ambiente docker disponibilizado para as aulas práticas não tenha sido alterado, utilizar as indicações de configuração de ambiente de trabalho das aulas práticas existente em <a href="<https://moodle.ensinolusofona.pt/mod/book/view.php?id=20951">Moodle</a>. <br>
    Caso se esteja a utiliza outro servidor de dados, utilizar dados de ligação e credenciais apropriadas
5. Executar os comandos do script SQL<br>
    Os comandos podem ser executados isoladamente ou em conjunto
    No final, verificar se todas as tabelas foram criadas consultando o INFORMATION_SCHEMA
6. Executar os comandos de importação de dados 
    Os comando de importação - *docker run -rm...* - são executados em terminal (_powershell_ em windows, _terminal_ em MacOS ou _Cli/Bash_ em Linux) e não em cliente de SQL, sempre na pasta onde se encontram os ficheiros csv:<br>
    1. Abrir uma janela de terminal, pode ser no terminal embutido do IDE ou no sistema operativo
    2. Navegar até à pasta onde estão os ficheiros CSV
       Caso se mantenha a estrutura de pastas do exemplo, o caminho será **(...)\DEISI_DB_2025\test-files\demofiles**<br>
    3. Executar os comandos de importação:<br>
       Exemplo:<br>
       ***docker run --rm -v ${PWD}:/work --entrypoint /opt/mssql-tools18/bin/bcp mcr.microsoft.com/mssql/server:2022-latest deisIMDB.dbo.movies in /work/movies.csv -S host.docker.internal,1433 -U sa -P "YourStrong!Passw0rd" -u -c -t "," -r "\n\r" -F 1***<br>
       **A importação pode ter erros** em resultado da codificação de ficheiros, particularmente do fim de linha<br>
       Caso ocorra um erro de caracter não reconhecido na importação, alterar a codificação (*encoding*) do ficheiro, que pode ser efectuada no próprio VSCode ou na maioria dos editores de texto
       1. Abrir o ficheiro no VSCode
       2. Seleccionar o controlo de codificação no canto inferior direito:
          ![alt text](images\image-3.png)
        3. Alterar a codificação conforme necessário 
        4. Gravar o ficheiro alterado
        4. O parametro *-r* do comando de importação tem que ser ajustado em concordância:
        - **CRLF** -> *-r "\n\r"*
        - **LF** -> *-r "\n"*
    4. Em caso de erro, pode-se acrescentar os parametros ***-m 1 -e /work/bcp_errors.log***<br>
       Esta opção no comando irá gerar um ficheiros ***bcp_errors.log*** com informação detalhada de eventuais erros que possam ter ocorrido na importação<br>

    No final, verificar se todos os dados foram importado correctamente utilizando comandos SELECT

## Código fonte

Ficheiros
- **[Dao.java](#classe-dao)** <span style="font-size:66%">(clicar no nome do ficheiros para ver detalhes da classe)</span> <br>
  Classe com especificações para ligar a base de dados
  Para simplificação do código e segurança, utiliza-se esta classe para centralizar o código próprio para ligaçao a base de dados, facilitando a leitura e compreenção do processo.
  A cada método que acede à base de dados, é instanciando um objecto desta classe, evitando a repetição de código e riscos de erros sintáticos
- **[Actor.java](#classe-actor)** <span style="font-size:66%">(clicar no nome do ficheiros para ver detalhes da classe)</span><br>
  Classe de definição de actor
  Contém atributos de actor e métodos de exemplificação de ligação a base de dados (CRUD)
  No âmbito do exemplo, os atributos mapeiam directamente os dados obtidos da importação dos ficheiros CSV, acrescendo-se apenas um atributo de estado necessário para implementação do comportamento de ***"undelete"*** (desactivar registos em vez de os eliminar)
  Explicação em detalhe mais abaixo
- **Main.java** <br>
  Classe principal de aplicações estáticas de Java
  Faz a interface de utilizador e executa as funções de calculo
- **Result.java** <br>
  Classe para apresentação de resultados de cada funcionalidade da aplicação
- **TipoEntidade.java** <br>
  Enumerado com as classes utilizaveis na aplicação

## Principais Classes de exemplo: 
<img src="images\output.png" alt="Diagrama de Classes" width="40%"><br>

### Classe: DAO
A classe tem com atributos os elementos necessários para realizar a ligaçao a uma base de dados MS-SQL:
    sqlserver: endereço do servidor
    databaseName: nome da base de dados para a ligação
    user: nome de utilizador
    password: palavra-passe de [user]
    encrypt: definição de encriptação da ligação ([default]true/false)
    trustServerCertificate: Confiabilidade do certificado de utilizador

O método getConnection() devolve um objecto de ligaçao a base de dados com as caracteristicas definidas na classe

Os parametros da classe correspondem aos da instalação base utilizada nas aulas, mas pode ser criada um construtor em *overload* caso se pretenda que o objecto seja criado com caracteristicas diferentes dos iniciais ou mesmo com parametros definidos na instanciação

### Classe: Actor
A classe utiliza dois construtores:<br>
*Actor(int actorId, String actorName, char actorGender, int movieId)*<br>
- Cria um novo objecto com dados inseridos pelo utilizador <br>

*Actor(int actorId)*<br>
- Carrega o actor da base de dados, a partir o seu id

O principal objectivo desta classe é demonstrar o acesso a bases de dados utilizando uma linguagem de programação, no caso, o **JAVA**<br>
Para este efeito, apresentam-se quatro métodos para demonstrar as acções base de operação com uma base de dados, normalmente designadas por ***CRUD*** ou ***Create, Read, Update*** e ***Delete***<br>
Os métodos em questão são:<br>
**CREATE** <span style="font-size:66%">(INSERT)</span>: *insertDB()*<br>
- Insere o actor na base de dados. Não utiliza argumentos por ser um método interno da classe<br>
No código apresentam-se dois exemplos de inserção: um com **INSERT** simples (comentado), que representa a forma mais convencional e comum de inserção em base de dados; outro com chamada - ***CALL*** - a procedimento para lidar com actores inactivos. Caso o utilizador não exista, é inserido; caso exista mas esteja oculto/inactivo, o procedimento limita-se a reactivá-lo. [ver a descrição do exemplo para mais detalhes](#utilização-de-exemplos). <br>
A versão comentada é mantida para maior abrangência do exemplo, servindo para demonstrar a operação de escrita básica em base de dados<br>
No mesmo sentido, a versão com a chamada a procedimento serve para demonstrar a utilização de procedimentos de SQL (***Stored Procedures***) a partir de programação Java

**READ** <span style="font-size:66%">(SELECT)</span>: *Actor(int actorId)*<br>
- Método constructor já referido anteriormente, responsável por ler os dados de um actor a partir da base de dados

**UPDATE**: *updateDB()*
- Actualiza o actor na base de dados. Não utiliza argumentos por ser um método interno da classe<br>
Note-se que o método escreve todos os atributos do actor, mesmo que não tenha sido alterados. Identificar alterações implicaria maior complexidade no código sem qualquer ganho em termos de desempenho, pelo contrário.

**DELETE**: *deleteDB()*
- Apaga o actor na base de dados. Não utiliza argumentos por ser um método interno da classe<br>
Conforme indicado no comentário do método (no código), a base de dados tem um *trigger* que *transforma* a eliminação - ***delete*** - na inactivação do actor, não o apagando efectivamente, processo vulgarmente designado por ***UNDELETE***<br>
De modo a garantir a integridade e coerencia de dados, este mesmo *trigger* **só elimina actores caso <u>não tenham filmes associados</u>**

Todos os métodos utilizam estrutura base para ligação a base de dados:

1. Variavel *Dao*, que é um atributo da classe instanciado nos construtores
2. *String* com comando a executar na base de dados<br>
A string utiliza ***placeholder*** (caracter '**?**') para posicionar parametros a utilizar na chamada à base de dados<br>
De notar que se utiliza sintaxe do Java e não de SQL, embora nalguns casos seja igual
3. Variavel do tipo *Connection* responsável pela ligação à base de dados<br>
Esta variavel é instanciada a partir do dao da classe<br>
4. Preparação da instrução (e.g. ***PreparedStatement***), que pode ter variações consoante a instrução a executar
5. Definição de parametros, por substituição dos ***placeholder*** <br>
Instrução do ***statement.set\[tipo](«ordem»,«valor»)*** onde:<br>
**tipo** identifica o tipo da variavel a atribuir ao parametro<br>
**ordem** indica a ordem ***placeholder*** na *String* de SQL<br>
**valor** é o valor do parametro na chamada à base de dados
6. Execução da instrução com comando do tipo ***statement.execute\[acção]()***<br>
**acção** pode ser diferente consoante a instrução realizada<br>
Caso se pretenda utilizar dados devolvidos pela base de dados, terá que se atribuir os resultados da execução da instrução numa variavel local do tipo ***resultSet***
7. Reter os valores que possam ter sido devolvidos pela base de dados utilzando uma instrução<br> ***«variave_local» = resultSet.get\[tipo]("«nome:coluna»");***<br>
Este formato apenas funciona com comando ***executeQuery***

## Outros exemplos de utilização de código SQL
### Chamada a função escalar (devolve apenas um valor)
***actorExists(int actorID)*** <span style="font-size:66%">(linha 98 no ficheiro Main.java)</span><br>
**Principais caracateristicas**<br>
1. A instrução de chamada à função tem um ***placeholder*** para receber o resultado da função
2. O registo do parametro que recebe o resultado é efectuado com ***registerOutParameter*** e não com **set\[tipo]**
3. O tipo de dados de retorno é definido como parametro ***registerOutParameter***
### Chamada a função vectorial (devolve uma tabela)
***listaActores()*** <span style="font-size:66%">(linha 170 no ficheiro Main.java)</span><br>
**Principais caracateristicas**<br>
1. Sendo o resultado uma tabela, a estrutura base da chamada é igual ao que acontece com um *SELECT* convencional descrito anteriormente no exemplo de ***READ*** de **CRUD**
2. Utiliza-se um ciclo ***While*** para iterar sobre o ***resultSet*** 
3. A iteração é efectuada pelo método ***next()*** do próprio ***resultSet*** 
### Chamada a um procedimento
***insertDB()*** <span style="font-size:66%">(linha 107 no ficheiro Actor.java)</span><br>
**Principais caracateristicas**<br>
\-- descrito anteriormente no exemplo ***INSERT*** de **CRUD** \--
### Tratamento de erros
Todas as funções e procedimentos do exemplo utilizam estruturas básicas ***TRY ... CATCH***, em sintaxe Java, para os acessos a base de dados<br>
**IMPORTANTE**<br>
Ter em contao modo de tratamento de erros e alerta de eventos do SQL, conforme estudado em aula<br>
Java e SQL executam em ambitos diferentes e com tratamento de excepções e erros inteiramente independentes, pelo que falhas na comunicação de eventos podem gerar comportamentos idesejados
Também se deve ter em conta que excepções geradas programáticamente por aplicação de regras de negócio ou requisitos funcionais têm tratamento diferente das que são geradas por erro de execução 

## Utilização do exemplo demonstratico

O exemplo disponibilizado inclui referência todas as funções solicitadas no projecto base de AED, mas também algumas que se pode antecipar que venham a ser desenvolvidas na cadeira de Base de Dados.
Todas estas funcionalidades são listadas em menus de opções, carregado no lançamento da aplicação ou sempre que se seleccionar a opção "Help", mas apenas algumas estão implementadas, apenas as necessárias para exemplificação de acesso a bases de dados utilizando Java.
Assim, as opções com o prefixo '*' não estão desenvolvidas

Para os melhores resultados do exemplo, os dados já devem estar carregados para a base de dados. Preferencialmente apenas se devem carregar os dados de demonstração, para evitar que a experiência 'danifique' os dados reais a utilizar no projecto

O modo mais eficaz para verificar o funcionamento será realizar a seguite chamada de funcionalidades:

1. Listar actores **(LISTAR_ACTORES)**<br>
    A lista deve conter exactamente os actores importados do ficheiro CSV
2. Inserir dois actores **(INSERT_ACTOR «id»;«name»;«gender»;«movie-id»)**<br>
   Um dos actore deve ter filme associado; o segundo não tem filme
   - Pode ser necessário alterar a tabela de actores para aceitar nulos na coluna de filmes
    A cada actor inserido, a aplicação irá confirmar a sua existência e listar os seus dados
    No actor duplicado será dada a indicação que já existe, os dados apresentados serão os existentes na base de dados e não o inseridos
3. Inserir actor duplicado **(INSERT_ACTOR «id»;«name»;«gender»;«movie-id»)**<br>  
   - Sugestão: duplicar apenas o ID, os restantes dados devem ser diferentes
    A a aplicação irá confirmar que o actor já existe e listar os seus dados, os dados apresentados serão os existentes na base de dados e não o inseridos
4. Listar actores **(LISTAR_ACTORES)**<br>
    A lista deve conter os dois novos actores inseridos, sem alteraçoes no dulicado
5. Alterar um actor **(UPDATE_ACTOR «id»;«name»;«gender»;«movie-id»)**<br>
   - Sugestão: alterar o actor da duplicaçao em 2
    A aplicaçao confirma existência e apresenta dados actualizado
6. Alterar um actor inexistente **(UPDATE_ACTOR «id»;«name»;«gender»;«movie-id»)**<br>
    Indicação de actor inexistente
7. Eliminar os dois actores inseridos em 2 **(APAGAR_ACTOR «id»)**<br>
    A cada actor, a aplicaçao confirma a eliminação sem diferenças visiveis  
8. Listar actores **(LISTAR_ACTORES)**<br>
    Nenhum do dois actores eliminados é apresentado
9. Numa sessão de SQL, consultar a tabela de actores (este teste não é realizado na aplicação JAVA)
    O actor com filme associado deve estar inactivo (isActive = 0) mas existe na tabela.
    O actor sem filme associado não será listado
10. Inserir novamente o actor eliminado **(INSERT_ACTOR «id»;«name»;«gender»;«movie-id»)**<br>
    - Apenas interessa o actor com filme associado
    A aplicação informa que o actor já existe e que o irá carregar da base de dados, informação semelhante à apresentada numa inserção de raiz
11. Listar actores **(LISTAR_ACTORES)**<br>
    O actor 'activado' é apresentado na lista
12. Numa sessão de SQL, consultar a tabela de actores (este teste não é realizado na aplicação JAVA)
    O actor 'inserido' estará activo (isActive = 1) na tabela.

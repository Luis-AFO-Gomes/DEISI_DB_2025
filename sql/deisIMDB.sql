use deisIMDB
GO

IF EXISTS (SELECT *
           FROM   sys.objects
           WHERE  object_id = OBJECT_ID(N'[dbo].[ufn_actorIdsOcupados]')
                  AND type = N'FN')
	DROP FUNCTION [dbo].[ufn_actorIdsOcupados];
GO

CREATE FUNCTION [dbo].[ufn_actorIdsOcupados](
		@actorID NUMERIC(7)
		) RETURNS BIT
	AS
	BEGIN
	RETURN (SELECT CASE 
                    WHEN EXISTS (SELECT 1
                                 FROM   dbo.actors
                                 WHERE  actorID = @actorID AND 
                                        isActive = 1) THEN 1
                    ELSE 0
               END )
	END;
GO

SELECT * FROM movies;

SELECT * FROM dbo.actors ORDER BY actorID;

/************************************************************
 * TRIGGER para evitar eliminação de actores com filmes     *
 ************************************************************/
-- Variante 3: Ocultar actores em vez de os eliminar
-- antes de mais, vamos precisar de uma nova coluna na tabela actors para indicar se o actor está activo ou não
ALTER TABLE dbo.actors
     ADD isActive BIT NOT NULL 
     CONSTRAINT DF_actors_isActive DEFAULT (1); -- Não esquecer o DEFAULT uma vez que a tabela já tem dados
GO

IF EXISTS (SELECT *
           FROM   sys.objects
           WHERE  object_id = OBJECT_ID(N'[dbo].[tr_NoDelActor]')
                  AND type = N'TR')
	DROP TRIGGER [dbo].[tr_NoDelActor];
GO

CREATE TRIGGER [dbo].[tr_NoDelActor]
ON [dbo].[actors]
INSTEAD OF DELETE
AS 
BEGIN
     SET NOCOUNT ON;

     -- Debug logging (informational)
     DECLARE @actorIDs nvarchar(max) = 
     (
          SELECT
               STRING_AGG(CAST(actorID AS nvarchar(20)), ',')
               WITHIN GROUP (ORDER BY actorID)
               FROM deleted)
     ;

     RAISERROR(N'Atores a eliminar: %s', 10, 1, @actorIDs);

     IF EXISTS (SELECT 1
                    FROM deleted d
                    WHERE EXISTS   (SELECT 1
                                        FROM dbo.actors a
                                        WHERE a.actorID = d.actorID AND 
                                             a.movieID IS NOT NULL
                                   ) OR
                         d.movieID IS NOT NULL
               )
     BEGIN
          UPDATE dbo.actors
               SET isActive = 0
               FROM dbo.actors a
               INNER JOIN deleted d
                    ON a.actorID = d.actorID;
     END 
     ELSE
     BEGIN
          DELETE a
               FROM dbo.actors a
               INNER JOIN deleted d
                    ON a.actorID = d.actorID;     
     END 
END;
GO

-- Esta variante tem uma complexidade acrescida para a restante solução, uma vez que implica alterar leitura e inserção de actores
-- A primeira deve passar a ser realizada por uma função vectorial, 
-- já a segunda, deve passar a ser feita por uma stored procedure que verifique se o actor já existe e está inactivo, 
-- para o reactivar em vez de inserir um novo registo
-- 1. Função para obter actores activos
IF EXISTS (SELECT *
           FROM   sys.objects
           WHERE  object_id = OBJECT_ID(N'[dbo].[tf_getActiveActors]')
                  AND type = N'FT')
     DROP FUNCTION [dbo].[tf_getActiveActors];
GO

CREATE FUNCTION [dbo].[tf_getActiveActors]()
RETURNS TABLE 
AS
RETURN
(
    SELECT DISTINCT actorID,actorName,actorGender FROM dbo.actors WHERE isActive = 1
);
GO   

SELECT * FROM dbo.tf_getActiveActors();

-- 2. SP para inserir actores
IF EXISTS (SELECT * 
           FROM   sys.objects
           WHERE  object_id = OBJECT_ID(N'[dbo].[sp_insertActor]')
                  AND type = N'P')
     DROP PROCEDURE [dbo].[sp_insertActor];  
GO

CREATE PROCEDURE [dbo].[sp_insertActor] 
    @actorID NUMERIC(7),
    @actorName NVARCHAR(100),
    @actorGender CHAR(1),
    @movieID INT = NULL       
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 
                   FROM dbo.actors 
                   WHERE actorID = @actorID AND isActive = 0)
    BEGIN
        -- Reactivar actor
        UPDATE dbo.actors
            SET actorName = @actorName,
                actorGender = @actorGender,
                movieID = @movieID,
                isActive = 1
        WHERE actorID = @actorID;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.actors (actorID, actorName, actorGender, movieID, isActive)
        VALUES (@actorID, @actorName, @actorGender, @movieID, 1);
    END
END;
GO       

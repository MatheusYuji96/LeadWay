-- Execute uma vez no banco leadway antes de iniciar o backend atualizado.
USE leadway;
ALTER TABLE leads ADD COLUMN vinculado_em DATETIME NULL;
-- Registros antigos não têm histórico confiável da data da vinculação.
-- Por isso, permanecem NULL até que o responsável seja atribuído/alterado.

-- Executar uma única vez no banco leadway, após conferir se a coluna não existe.
USE leadway;
ALTER TABLE vendedor ADD COLUMN regiao VARCHAR(100) NULL;

-- ========================================================
-- MIGRACAO: integracao com Cal.com
-- Cria apenas a tabela com a "chave" do Cal.com de cada vendedor.
-- NAO existe tabela de reunioes: os compromissos ficam no Cal.com.
-- (Com spring.jpa.hibernate.ddl-auto=update o Hibernate cria a tabela sozinho;
--  este script e para quem controla o schema na mao.)
-- NAO apaga dados existentes.
-- ========================================================

USE leadway;

CREATE TABLE IF NOT EXISTS cal_integracao (
    vendedor_id INT PRIMARY KEY,
    cal_username VARCHAR(100) NOT NULL,
    cal_api_key VARCHAR(255) NOT NULL,
    conectado_em DATETIME NOT NULL,

    CONSTRAINT fk_cal_integracao_vendedor
        FOREIGN KEY (vendedor_id)
        REFERENCES vendedor(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

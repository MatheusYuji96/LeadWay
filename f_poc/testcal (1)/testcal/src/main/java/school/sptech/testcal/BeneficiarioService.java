package school.sptech.testcal;
import school.sptech.testcal.Beneficiario;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

    @Service
    public class BeneficiarioService {

        private final Map<Long, Beneficiario> database = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1);

        public Beneficiario save(Beneficiario beneficiario) {
            if (beneficiario.getId() == null) {
                beneficiario.setId(idGenerator.getAndIncrement());
            }
            database.put(beneficiario.getId(), beneficiario);
            return beneficiario;
        }

        public Beneficiario findById(Long id) {
            return database.get(id);
        }
    }

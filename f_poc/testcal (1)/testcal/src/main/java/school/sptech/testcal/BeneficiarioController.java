package school.sptech.testcal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiarios")
@CrossOrigin(origins = "http://localhost:5174") // Habilita o CORS para o Vite React
public class BeneficiarioController {

    private final BeneficiarioService beneficiarioService;

    public BeneficiarioController(BeneficiarioService beneficiarioService) {
        this.beneficiarioService = beneficiarioService;
    }

    @PostMapping
    public ResponseEntity<Beneficiario> createBeneficiary(@RequestBody Beneficiario beneficiario) {
        Beneficiario criado = beneficiarioService.save(beneficiario);
        return ResponseEntity.ok(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beneficiario> pesquisarBeneficiarioPorId(@PathVariable Long id) {
        Beneficiario beneficiario = beneficiarioService.findById(id);
        if (beneficiario == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(beneficiario);
    }
}

package br.com.nf.nfapi.repository;

import br.com.nf.nfapi.entity.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    boolean existsByNumero(String numero);
}

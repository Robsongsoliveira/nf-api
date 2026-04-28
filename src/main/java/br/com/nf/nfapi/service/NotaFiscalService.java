package br.com.nf.nfapi.service;

import br.com.nf.nfapi.entity.Cliente;
import br.com.nf.nfapi.entity.ItemNotaFiscal;
import br.com.nf.nfapi.entity.NotaFiscal;
import br.com.nf.nfapi.entity.Produto;
import br.com.nf.nfapi.repository.ClienteRepository;
import br.com.nf.nfapi.repository.NotaFiscalRepository;
import br.com.nf.nfapi.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public NotaFiscalService(
            NotaFiscalRepository notaFiscalRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository
    ) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public NotaFiscal criar(NotaFiscal notaFiscal) {

        if (notaFiscal == null) {
            throw new RuntimeException("Nota fiscal inválida (null).");
        }

        if (notaFiscal.getNumero() == null || notaFiscal.getNumero().isBlank()) {
            throw new RuntimeException("Número da nota é obrigatório.");
        }

        if (notaFiscalRepository.existsByNumero(notaFiscal.getNumero())) {
            throw new RuntimeException("Já existe nota fiscal com o número: " + notaFiscal.getNumero());
        }

        if (notaFiscal.getCliente() == null || notaFiscal.getCliente().getId() == null) {
            throw new RuntimeException("Cliente é obrigatório (informe cliente.id).");
        }

        Long clienteId = notaFiscal.getCliente().getId();
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clienteId));
        notaFiscal.setCliente(cliente);

        List<ItemNotaFiscal> itens = notaFiscal.getItens();
        if (itens == null || itens.isEmpty()) {
            throw new RuntimeException("A nota fiscal precisa ter pelo menos 1 item.");
        }

        for (ItemNotaFiscal item : itens) {

            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new RuntimeException("Quantidade inválida no item.");
            }

            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new RuntimeException("Produto é obrigatório em cada item.");
            }

            Long produtoId = item.getProduto().getId();
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));

            item.setProduto(produto);
            item.setNotaFiscal(notaFiscal);

            BigDecimal preco = produto.getValorUnitario();
            if (preco == null) {
                throw new RuntimeException("Produto sem valorUnitario: " + produtoId);
            }

            BigDecimal totalItem = preco.multiply(BigDecimal.valueOf(item.getQuantidade()));
            item.setValorTotal(totalItem.doubleValue());
        }

        notaFiscal.calcularValorTotal();

        return notaFiscalRepository.save(notaFiscal);
    }

    public List<NotaFiscal> listar() {
        List<NotaFiscal> notas = notaFiscalRepository.findAll();

        notas.forEach(NotaFiscal::calcularValorTotal);

        return notas;
    }

    public NotaFiscal buscarPorId(Long id) {
        NotaFiscal nota = notaFiscalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota fiscal não encontrada: " + id));

        nota.calcularValorTotal();

        return nota;
    }

    public NotaFiscal atualizar(Long id, NotaFiscal novaNota) {

        NotaFiscal notaExistente = notaFiscalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota fiscal não encontrada: " + id));

        if (novaNota.getNumero() == null || novaNota.getNumero().isBlank()) {
            throw new RuntimeException("Número da nota é obrigatório.");
        }

        notaExistente.setNumero(novaNota.getNumero());
        notaExistente.setFornecedor(novaNota.getFornecedor());
        notaExistente.setDataNota(novaNota.getDataNota());

        if (novaNota.getCliente() == null || novaNota.getCliente().getId() == null) {
            throw new RuntimeException("Cliente é obrigatório.");
        }

        Cliente cliente = clienteRepository.findById(novaNota.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        notaExistente.setCliente(cliente);
        notaExistente.getItens().clear();

        for (ItemNotaFiscal item : novaNota.getItens()) {

            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new RuntimeException("Quantidade inválida.");
            }

            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new RuntimeException("Produto é obrigatório.");
            }

            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            item.setProduto(produto);
            item.setNotaFiscal(notaExistente);

            BigDecimal preco = produto.getValorUnitario();
            BigDecimal total = preco.multiply(BigDecimal.valueOf(item.getQuantidade()));

            item.setValorTotal(total.doubleValue());

            notaExistente.getItens().add(item);
        }

        notaExistente.calcularValorTotal();

        return notaFiscalRepository.save(notaExistente);
    }

    public void deletar(Long id) {

        NotaFiscal nota = notaFiscalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota fiscal não encontrada: " + id));

        notaFiscalRepository.delete(nota);
    }
}
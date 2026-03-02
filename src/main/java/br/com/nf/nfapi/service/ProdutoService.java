package br.com.nf.nfapi.service;

import br.com.nf.nfapi.entity.Produto;
import br.com.nf.nfapi.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto criar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    public Produto atualizar(Long id, Produto dados) {
        Produto existente = buscarPorId(id);
        existente.setCodigo(dados.getCodigo());
        existente.setDescricao(dados.getDescricao());
        existente.setValorUnitario(dados.getValorUnitario());
        return produtoRepository.save(existente);
    }

    public void excluir(Long id) {
        Produto existente = buscarPorId(id);
        produtoRepository.delete(existente);
    }
}

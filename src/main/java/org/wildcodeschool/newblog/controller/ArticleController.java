package org.wildcodeschool.newblog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcodeschool.newblog.dto.ArticleDTO;
import org.wildcodeschool.newblog.model.Article;
import org.wildcodeschool.newblog.model.Category;
import org.wildcodeschool.newblog.repository.ArticleRepository;
import org.wildcodeschool.newblog.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;


    public ArticleController(ArticleRepository articleRepository, CategoryRepository categoryRepository) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        List<ArticleDTO> articlesDTO = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articlesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if (!optionalArticle.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Article article = optionalArticle.get();

        ArticleDTO articleDTO = convertToDTO(article);

        return ResponseEntity.ok(articleDTO);
    }

    @GetMapping("/search-title")
    public ResponseEntity<List<Article>> getArticlesbyTitle(@RequestParam String searchTerms) {
        List<Article> articles = articleRepository.findByTitle(searchTerms);

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-content")
    public ResponseEntity<List<Article>> getArticlesByContent(@RequestParam String searchTerms) {
        List<Article> articles = articleRepository.findByContentContaining(searchTerms);

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-after")
    public ResponseEntity<List<Article>> getArticlesCreatedAfter(@RequestParam LocalDateTime date) {
        List<Article> articles = articleRepository.findByCreatedAtAfter(date);

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-last")
    public  ResponseEntity<List<Article>> getLastestArticles() {
        List<Article> articles = articleRepository.findTop3ByOrderByCreatedAtDesc();

        return ResponseEntity.ok(articles);
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody Article article) {
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        Category category = categoryRepository.findById(article.getCategory().getId()).orElse(null);

        article.setCategory(category);

        Article savedArticle = articleRepository.save(article);

        ArticleDTO articleDTO = convertToDTO(savedArticle);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if (!optionalArticle.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Article article = optionalArticle.get();

        article.setTitle(articleDetails.getTitle());
        article.setContent(articleDetails.getContent());

        article.setUpdatedAt(LocalDateTime.now());

        Category category = categoryRepository.findById(articleDetails.getCategory().getId()).orElse(null);

        article.setCategory(category);

        Article updatedArticle = articleRepository.save(article);

        ArticleDTO articleDTO = convertToDTO(updatedArticle);

        return ResponseEntity.ok(articleDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if (!optionalArticle.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        articleRepository.deleteById(id);

        return ResponseEntity.noContent().build();

    }

    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setCreatedAt(article.getCreatedAt());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        if (article.getCategory() != null) {
            articleDTO.setCategoryId(article.getCategory().getId());
        }
        return articleDTO;
    }
}

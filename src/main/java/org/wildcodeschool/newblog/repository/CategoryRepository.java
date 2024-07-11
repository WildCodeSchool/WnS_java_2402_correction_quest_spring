package org.wildcodeschool.newblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wildcodeschool.newblog.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

package com.ivotasevski.transoutbox.example.respository;

import com.ivotasevski.transoutbox.example.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleRepository extends JpaRepository<Example, Long> {
}

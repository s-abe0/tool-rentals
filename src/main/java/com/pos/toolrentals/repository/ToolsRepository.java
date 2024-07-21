package com.pos.toolrentals.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pos.toolrentals.entity.Tool;

@Repository
public interface ToolsRepository extends CrudRepository<Tool, Long> {
	public Optional<Tool> findByToolCode(String code);
}

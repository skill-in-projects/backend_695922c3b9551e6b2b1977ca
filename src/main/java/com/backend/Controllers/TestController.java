package com.backend.Controllers;

import com.backend.Models.TestProjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private EntityManager entityManager;

    @GetMapping
    public ResponseEntity<List<TestProjects>> getAll() {
        try {
            Query query = entityManager.createNativeQuery("SELECT \"Id\", \"Name\" FROM \"TestProjects\" ORDER BY \"Id\"", TestProjects.class);
            @SuppressWarnings("unchecked")
            List<TestProjects> projects = query.getResultList();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestProjects> getById(@PathVariable Integer id) {
        try {
            Query query = entityManager.createNativeQuery("SELECT \"Id\", \"Name\" FROM \"TestProjects\" WHERE \"Id\" = :id", TestProjects.class);
            query.setParameter("id", id);
            @SuppressWarnings("unchecked")
            List<TestProjects> results = query.getResultList();
            if (results.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(results.get(0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<TestProjects> create(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            Query query = entityManager.createNativeQuery("INSERT INTO \"TestProjects\" (\"Name\") VALUES (:name) RETURNING \"Id\", \"Name\"", TestProjects.class);
            query.setParameter("name", name);
            @SuppressWarnings("unchecked")
            List<TestProjects> results = query.getResultList();
            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestProjects> update(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            Query query = entityManager.createNativeQuery("UPDATE \"TestProjects\" SET \"Name\" = :name WHERE \"Id\" = :id RETURNING \"Id\", \"Name\"", TestProjects.class);
            query.setParameter("name", name);
            query.setParameter("id", id);
            @SuppressWarnings("unchecked")
            List<TestProjects> results = query.getResultList();
            if (results.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(results.get(0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Integer id) {
        try {
            Query query = entityManager.createNativeQuery("DELETE FROM \"TestProjects\" WHERE \"Id\" = :id");
            query.setParameter("id", id);
            int deleted = query.executeUpdate();
            if (deleted == 0) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

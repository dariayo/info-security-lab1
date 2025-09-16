package org.example.repository;

import org.example.models.DataItem;
import org.example.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataItemRepository extends JpaRepository<DataItem, Long> {
    List<DataItem> findByUser(User user);

    List<DataItem> findByUserUsername(String username);
}
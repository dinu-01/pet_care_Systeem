package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Find all feedback by owner
    List<Feedback> findByOwnerId(Long ownerId);

    // Find feedback by owner and category
    List<Feedback> findByOwnerIdAndCategory(Long ownerId, Feedback.FeedbackCategory category);

    // Find feedback by type
    List<Feedback> findByFeedbackType(Feedback.FeedbackType feedbackType);

    // Find feedback by category
    List<Feedback> findByCategory(Feedback.FeedbackCategory category);

    // Find feedback with rating greater than or equal to
    List<Feedback> findByRatingGreaterThanEqual(Integer rating);

    // Find recent feedback (last 30 days)
    @Query("SELECT f FROM Feedback f WHERE f.createdAt >= :since ORDER BY f.createdAt DESC")
    List<Feedback> findRecentFeedback(@Param("since") java.time.LocalDateTime since);

    // Count feedback by type
    long countByFeedbackType(Feedback.FeedbackType feedbackType);

    // Count feedback by category
    long countByCategory(Feedback.FeedbackCategory category);

    // Get average rating
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getAverageRating();

    // Check if owner has submitted feedback recently (prevent spam)
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.owner.id = :ownerId AND f.createdAt >= :since")
    long countRecentFeedbackByOwner(@Param("ownerId") Long ownerId, @Param("since") java.time.LocalDateTime since);
}
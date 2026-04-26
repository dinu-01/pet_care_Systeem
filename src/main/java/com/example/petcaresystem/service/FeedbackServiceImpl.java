package com.example.petcaresystem.service;

import com.example.petcaresystem.dto.CreateFeedbackRequest;
import com.example.petcaresystem.dto.FeedbackDTO;
import com.example.petcaresystem.model.Feedback;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.FeedbackRepository;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    // Anti-spam: Minimum time between feedback submissions (in hours)
    private static final int MIN_TIME_BETWEEN_FEEDBACK_HOURS = 0 ;

    @Override
    public FeedbackDTO createFeedback(CreateFeedbackRequest request) {
        try {
            log.info("Creating feedback for owner ID: {}", request.getOwnerId());

            // Validate owner exists
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found with id: " + request.getOwnerId()));

            // Check if user can submit feedback (anti-spam)
            if (!canSubmitFeedback(request.getOwnerId())) {
                throw new RuntimeException("Please wait before submitting another feedback. Minimum time between submissions is " +
                        MIN_TIME_BETWEEN_FEEDBACK_HOURS + " hour(s).");
            }

            // Create feedback entity
            Feedback feedback = Feedback.builder()
                    .feedbackType(request.getFeedbackType())
                    .rating(request.getRating())
                    .category(request.getCategory())
                    .description(request.getDescription())
                    .owner(owner)
                    .build();

            // Save feedback
            Feedback savedFeedback = feedbackRepository.save(feedback);
            log.info("✅ Feedback created successfully with ID: {}", savedFeedback.getId());

            return (FeedbackDTO) FeedbackDTO.fromEntity(savedFeedback);

        } catch (Exception e) {
            log.error("❌ Error creating feedback: {}", e.getMessage());
            throw new RuntimeException("Error creating feedback: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getAllFeedback() {
        try {
            log.info("Retrieving all feedback");
            List<Feedback> feedbackList = feedbackRepository.findAll();

            return feedbackList.stream()
                    .map(FeedbackDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving all feedback: {}", e.getMessage());
            throw new RuntimeException("Error retrieving feedback: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackDTO getFeedbackById(Long id) {
        try {
            log.info("Retrieving feedback by ID: {}", id);
            Feedback feedback = feedbackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

            return (FeedbackDTO) FeedbackDTO.fromEntity(feedback);
        } catch (Exception e) {
            log.error("Error retrieving feedback by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error retrieving feedback: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getFeedbackByOwner(Long ownerId) {
        try {
            log.info("Retrieving feedback for owner ID: {}", ownerId);

            // Verify owner exists
            if (!userRepository.existsById(ownerId)) {
                throw new RuntimeException("Owner not found with id: " + ownerId);
            }

            List<Feedback> feedbackList = feedbackRepository.findByOwnerId(ownerId);

            return feedbackList.stream()
                    .map(FeedbackDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving feedback for owner {}: {}", ownerId, e.getMessage());
            throw new RuntimeException("Error retrieving feedback: " + e.getMessage());
        }
    }

    @Override
    public FeedbackDTO updateFeedback(Long id, CreateFeedbackRequest request) {
        try {
            log.info("Updating feedback with ID: {}", id);

            Feedback existingFeedback = feedbackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

            // Verify the requester owns this feedback
            if (!existingFeedback.getOwner().getId().equals(request.getOwnerId())) {
                throw new RuntimeException("You can only update your own feedback");
            }

            // Update fields
            existingFeedback.setFeedbackType(request.getFeedbackType());
            existingFeedback.setRating(request.getRating());
            existingFeedback.setCategory(request.getCategory());
            existingFeedback.setDescription(request.getDescription());

            Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
            log.info("✅ Feedback updated successfully: {}", id);

            return (FeedbackDTO) FeedbackDTO.fromEntity(updatedFeedback);

        } catch (Exception e) {
            log.error("Error updating feedback with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating feedback: " + e.getMessage());
        }
    }

    @Override
    public void deleteFeedback(Long id) {
        try {
            log.info("Deleting feedback with ID: {}", id);

            if (!feedbackRepository.existsById(id)) {
                throw new RuntimeException("Feedback not found with id: " + id);
            }

            feedbackRepository.deleteById(id);
            log.info("✅ Feedback deleted successfully: {}", id);

        } catch (Exception e) {
            log.error("Error deleting feedback with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting feedback: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getFeedbackByType(Feedback.FeedbackType type) {
        try {
            log.info("Retrieving feedback by type: {}", type);
            List<Feedback> feedbackList = feedbackRepository.findByFeedbackType(type);

            return feedbackList.stream()
                    .map(FeedbackDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving feedback by type {}: {}", type, e.getMessage());
            throw new RuntimeException("Error retrieving feedback: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getFeedbackByCategory(Feedback.FeedbackCategory category) {
        try {
            log.info("Retrieving feedback by category: {}", category);
            List<Feedback> feedbackList = feedbackRepository.findByCategory(category);

            return feedbackList.stream()
                    .map(FeedbackDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving feedback by category {}: {}", category, e.getMessage());
            throw new RuntimeException("Error retrieving feedback: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getFeedbackByRating(Integer minRating) {
        try {
            log.info("Retrieving feedback with rating >= : {}", minRating);
            List<Feedback> feedbackList = feedbackRepository.findByRatingGreaterThanEqual(minRating);

            return feedbackList.stream()
                    .map(FeedbackDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving feedback by rating {}: {}", minRating, e.getMessage());
            throw new RuntimeException("Error retrieving feedback: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getRecentFeedback(int days) {
        try {
            log.info("Retrieving recent feedback from last {} days", days);
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            List<Feedback> feedbackList = feedbackRepository.findRecentFeedback(since);

            return feedbackList.stream()
                    .map(FeedbackDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving recent feedback: {}", e.getMessage());
            throw new RuntimeException("Error retrieving feedback: " + e.getMessage());
        }
    }

    @Override
    public boolean canSubmitFeedback(Long ownerId) {
        try {
            LocalDateTime since = LocalDateTime.now().minusHours(MIN_TIME_BETWEEN_FEEDBACK_HOURS);
            long recentCount = feedbackRepository.countRecentFeedbackByOwner(ownerId, since);
            return recentCount == 0;
        } catch (Exception e) {
            log.error("Error checking if user can submit feedback: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getFeedbackStatistics() {
        try {
            log.info("Generating feedback statistics");
            Map<String, Object> stats = new HashMap<>();

            // Basic counts
            long totalFeedback = feedbackRepository.count();
            long anonymousCount = feedbackRepository.countByFeedbackType(Feedback.FeedbackType.ANONYMOUS);
            long identifiedCount = feedbackRepository.countByFeedbackType(Feedback.FeedbackType.IDENTIFIED);

            // Average rating
            Double averageRating = feedbackRepository.getAverageRating();

            // Category counts
            Map<String, Long> categoryCounts = new HashMap<>();
            Arrays.stream(Feedback.FeedbackCategory.values()).forEach(category -> {
                long count = feedbackRepository.countByCategory(category);
                categoryCounts.put(category.name(), count);
            });

            // Recent feedback (last 7 days)
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            List<Feedback> recentFeedback = feedbackRepository.findRecentFeedback(weekAgo);

            stats.put("total", totalFeedback);
            stats.put("anonymous", anonymousCount);
            stats.put("identified", identifiedCount);
            stats.put("averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0);
            stats.put("categoryCounts", categoryCounts);
            stats.put("recentCount", recentFeedback.size());

            return stats;

        } catch (Exception e) {
            log.error("Error generating feedback statistics: {}", e.getMessage());
            throw new RuntimeException("Error generating statistics: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Feedback findFeedbackEntityById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return feedbackRepository.existsById(id);
    }
}
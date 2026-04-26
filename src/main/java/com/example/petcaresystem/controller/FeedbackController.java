package com.example.petcaresystem.controller;

import com.example.petcaresystem.dto.CreateFeedbackRequest;
import com.example.petcaresystem.dto.FeedbackDTO;
import com.example.petcaresystem.model.Feedback;
import com.example.petcaresystem.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/feedback")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    // ✅ Health Check
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ Feedback API is healthy!");
    }

    // ✅ Create Feedback (Pet Owner)
    @PostMapping("/create")
    public ResponseEntity<?> createFeedback(@Valid @RequestBody CreateFeedbackRequest request) {
        try {
            log.info("📝 Creating new feedback for owner ID: {}", request.getOwnerId());
            FeedbackDTO createdFeedback = feedbackService.createFeedback(request);
            return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("❌ Error creating feedback: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error creating feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating feedback: " + e.getMessage());
        }
    }

    // ✅ Get All Feedback (Admin)
    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedback() {
        try {
            log.info("📋 Retrieving all feedback");
            List<Object> feedbackList = feedbackService.getAllFeedback();
            return ResponseEntity.ok(feedbackList);
        } catch (Exception e) {
            log.error("❌ Error retrieving all feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving feedback: " + e.getMessage());
        }
    }

    // ✅ Get Feedback by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        try {
            log.info("🔍 Retrieving feedback by ID: {}", id);
            FeedbackDTO feedback = feedbackService.getFeedbackById(id);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            log.error("❌ Error retrieving feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error retrieving feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving feedback: " + e.getMessage());
        }
    }

    // ✅ Get Feedback by Owner (Pet Owner)
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getFeedbackByOwner(@PathVariable Long ownerId) {
        try {
            log.info("👤 Retrieving feedback for owner ID: {}", ownerId);
            List<Object> feedbackList = feedbackService.getFeedbackByOwner(ownerId);
            return ResponseEntity.ok(feedbackList);
        } catch (RuntimeException e) {
            log.error("❌ Error retrieving owner feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error retrieving owner feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving feedback: " + e.getMessage());
        }
    }

    // ✅ Update Feedback (Pet Owner - only their own)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long id, @Valid @RequestBody CreateFeedbackRequest request) {
        try {
            log.info("🔄 Updating feedback with ID: {}", id);
            FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, request);
            return ResponseEntity.ok(updatedFeedback);
        } catch (RuntimeException e) {
            log.error("❌ Error updating feedback: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error updating feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating feedback: " + e.getMessage());
        }
    }

    // ✅ Delete Feedback (Admin or Owner)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        try {
            log.info("🗑️ Deleting feedback with ID: {}", id);
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok("✅ Feedback deleted successfully");
        } catch (RuntimeException e) {
            log.error("❌ Error deleting feedback: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error deleting feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting feedback: " + e.getMessage());
        }
    }

    // ✅ Get Feedback by Type
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getFeedbackByType(@PathVariable Feedback.FeedbackType type) {
        try {
            log.info("📊 Retrieving feedback by type: {}", type);
            List<Object> feedbackList = feedbackService.getFeedbackByType(type);
            return ResponseEntity.ok(feedbackList);
        } catch (Exception e) {
            log.error("❌ Error retrieving feedback by type: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving feedback: " + e.getMessage());
        }
    }

    // ✅ Get Feedback by Category
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getFeedbackByCategory(@PathVariable Feedback.FeedbackCategory category) {
        try {
            log.info("📂 Retrieving feedback by category: {}", category);
            List<Object> feedbackList = feedbackService.getFeedbackByCategory(category);
            return ResponseEntity.ok(feedbackList);
        } catch (Exception e) {
            log.error("❌ Error retrieving feedback by category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving feedback: " + e.getMessage());
        }
    }

    // ✅ Get Feedback Statistics (Admin)
    @GetMapping("/statistics")
    public ResponseEntity<?> getFeedbackStatistics() {
        try {
            log.info("📈 Generating feedback statistics");
            Map<String, Object> statistics = feedbackService.getFeedbackStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("❌ Error generating statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating statistics: " + e.getMessage());
        }
    }

    // ✅ Check if user can submit feedback
    @GetMapping("/can-submit/{ownerId}")
    public ResponseEntity<?> canSubmitFeedback(@PathVariable Long ownerId) {
        try {
            log.info("🔍 Checking if owner {} can submit feedback", ownerId);
            boolean canSubmit = feedbackService.canSubmitFeedback(ownerId);
            return ResponseEntity.ok(Map.of("canSubmit", canSubmit));
        } catch (Exception e) {
            log.error("❌ Error checking feedback submission: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking submission: " + e.getMessage());
        }
    }

    // ✅ Get Recent Feedback
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentFeedback(@RequestParam(defaultValue = "7") int days) {
        try {
            log.info("🕒 Retrieving recent feedback from last {} days", days);
            List<Object> recentFeedback = feedbackService.getRecentFeedback(days);
            return ResponseEntity.ok(recentFeedback);
        } catch (Exception e) {
            log.error("❌ Error retrieving recent feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving recent feedback: " + e.getMessage());
        }
    }
}
package com.example.petcaresystem.service;

import com.example.petcaresystem.dto.CreateFeedbackRequest;
import com.example.petcaresystem.dto.FeedbackDTO;
import com.example.petcaresystem.model.Feedback;

import java.util.List;
import java.util.Map;

public interface FeedbackService {

    // CRUD Operations
    FeedbackDTO createFeedback(CreateFeedbackRequest request);
    List<Object> getAllFeedback();
    FeedbackDTO getFeedbackById(Long id);
    List<Object> getFeedbackByOwner(Long ownerId);
    FeedbackDTO updateFeedback(Long id, CreateFeedbackRequest request);
    void deleteFeedback(Long id);


    // Query Operations
    List<Object> getFeedbackByType(Feedback.FeedbackType type);
    List<Object> getFeedbackByCategory(Feedback.FeedbackCategory category);
    List<Object> getFeedbackByRating(Integer minRating);
    List<Object> getRecentFeedback(int days);

    // Business Logic
    boolean canSubmitFeedback(Long ownerId);
    Map<String, Object> getFeedbackStatistics();

    // Utility Methods
    Feedback findFeedbackEntityById(Long id);
    boolean existsById(Long id);
}
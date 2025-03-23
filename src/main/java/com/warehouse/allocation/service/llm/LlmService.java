package com.warehouse.allocation.service.llm;

import com.warehouse.allocation.model.AllocationSet;
import com.warehouse.allocation.model.AllocationSummary;
import com.warehouse.allocation.model.Item;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    @Value("${llm.api-key}")
    private String apiKey;

    @Value("${llm.model:gpt-4-turbo}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public AllocationSummary generateAllocationSummary(AllocationSet allocationSet) {
        // Prepare allocation data
        String allocationData = prepareAllocationData(allocationSet);

        // Create prompt
        String prompt = "You are an expert warehouse allocation system analyst. " +
                "Given the following allocation data, provide:\n" +
                "1. A detailed step-by-step summary of what happened during the allocation\n" +
                "2. Suggestions for improving the allocation efficiency and effectiveness\n\n" +
                "Format your response as a JSON object with two keys: 'summary' and 'improvements'.\n" +
                "For 'summary', provide an array of step descriptions.\n" +
                "For 'improvements', provide an array of suggested improvements.\n\n" +
                "Allocation Data:\n" + allocationData;

        // Call OpenAI API
        String response = callOpenAI(prompt);

        // Parse and format the response
        try {
            // Extract JSON from the response (it might have markdown code blocks)
            String jsonStr = response;
            if (response.contains("```json")) {
                jsonStr = response.substring(response.indexOf("```json") + 7, response.lastIndexOf("```")).trim();
            } else if (response.contains("```")) {
                jsonStr = response.substring(response.indexOf("```") + 3, response.lastIndexOf("```")).trim();
            }

            JSONObject jsonResponse = new JSONObject(jsonStr);
            JSONArray summarySteps = jsonResponse.getJSONArray("summary");
            JSONArray improvements = jsonResponse.getJSONArray("improvements");

            // Create and return AllocationSummary
            return AllocationSummary.builder()
                    .allocationSet(allocationSet)
                    .summarySteps(summarySteps.toString())
                    .suggestedImprovements(improvements.toString())
                    .build();
        } catch (Exception e) {
            // Fallback to a simplified format if JSON parsing fails
            List<String> summarySteps = extractSummaryPoints(response);
            List<String> improvements = extractImprovement(response);

            JSONObject summaryJson = new JSONObject();
            summaryJson.put("summary", new JSONArray(summarySteps));
            summaryJson.put("improvements", new JSONArray(improvements));

            return AllocationSummary.builder()
                    .allocationSet(allocationSet)
                    .summarySteps(summaryJson.getJSONArray("summary").toString())
                    .suggestedImprovements(summaryJson.getJSONArray("improvements").toString())
                    .build();
        }
    }

    private String callOpenAI(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(message);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2048);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> responseMessage = (Map<String, Object>) choice.get("message");
            return (String) responseMessage.get("content");
        } catch (Exception e) {
            return "Error calling OpenAI API: " + e.getMessage();
        }
    }

    // Other methods are the same as before...
    private String prepareAllocationData(AllocationSet allocationSet) {
        // Implementation unchanged
        StringBuilder builder = new StringBuilder();

        // Add allocation set details
        builder.append("Allocation Set: ").append(allocationSet.getName()).append("\n");
        builder.append("Status: ").append(allocationSet.getStatus()).append("\n");
        // ... rest of the method remains the same

        return builder.toString();
    }

    private List<String> extractSummaryPoints(String text) {
        // Implementation unchanged
        List<String> points = new ArrayList<>();
        // ... rest of the method remains the same
        return points;
    }

    private List<String> extractImprovement(String text) {
        // Implementation unchanged
        List<String> points = new ArrayList<>();
        // ... rest of the method remains the same
        return points;
    }
}
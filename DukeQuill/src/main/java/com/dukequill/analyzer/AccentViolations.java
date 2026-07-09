package com.dukequill.analyzer;

public class AccentViolations {

    private final int fromPos;
    private final String message;
    private final String suggestion;
    private final String originalText;

    public AccentViolations(int fromPos, String message, String suggestion, String originalText){
        this.fromPos = fromPos;
        this.message = message;
        this.suggestion = suggestion;
        this.originalText = originalText;
    }

    public int getFromPos(){
        return fromPos;
    }

    public String getMessage(){
        return message;
    }

    public String getSuggestedReplacements(){
        return suggestion;
    }

    public String getOriginalText(){
        return originalText;
    }

}

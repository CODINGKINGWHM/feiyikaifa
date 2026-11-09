package com.codingkingwhm.feiyikaifa.data.model;

public final class ExhibitItem {
    private final long id;
    private final String name;
    private final String role;
    private final String region;
    private final String era;
    private final String story;
    private final String patternMeaning;
    private final int artVariant;
    private final long patternId;

    public ExhibitItem(long id, String name, String role, String region, String era,
                       String story, String patternMeaning, int artVariant, long patternId) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.region = region;
        this.era = era;
        this.story = story;
        this.patternMeaning = patternMeaning;
        this.artVariant = artVariant;
        this.patternId = patternId;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getRegion() { return region; }
    public String getEra() { return era; }
    public String getStory() { return story; }
    public String getPatternMeaning() { return patternMeaning; }
    public int getArtVariant() { return artVariant; }
    public long getPatternId() { return patternId; }
}

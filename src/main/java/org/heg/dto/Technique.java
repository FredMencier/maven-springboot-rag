package org.heg.dto;

public class Technique {
    public String nameJapanese;
    public String category;
    public String beltLevel;

    public Technique() {}

    public Technique(String nameJapanese, String category, String beltLevel) {
        this.nameJapanese = nameJapanese;
        this.category = category;
        this.beltLevel = beltLevel;
    }

    @Override
    public String toString() {
        return "Technique{" +
                "nameJapanese='" + nameJapanese + '\'' +
                ", category='" + category + '\'' +
                ", beltLevel='" + beltLevel + '\'' +
                '}';
    }
}

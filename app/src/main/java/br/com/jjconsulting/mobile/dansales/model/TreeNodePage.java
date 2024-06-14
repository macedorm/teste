package br.com.jjconsulting.mobile.dansales.model;

public class TreeNodePage {

    private String label;
    private int initialIndex;
    private int finalIndex;
    private int level;

    public TreeNodePage(int initialIndex, int finalIndex, int level, String label) {
        this.initialIndex = initialIndex;
        this.finalIndex = finalIndex;
        this.level = level;
        this.label = label;
    }

    public String getLabel() {
        if (label == null) {
            return String.format("Registros de %d até %d", initialIndex + 1, finalIndex + 1);
        } else {
            return String.format("%s (%d - %d)", label, initialIndex + 1, finalIndex + 1);
        }
    }

    public int getInitialIndex() {
        return initialIndex;
    }

    public int getFinalIndex() {
        return finalIndex;
    }

    public int getLevel() {
        return level;
    }
}

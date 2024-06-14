package br.com.jjconsulting.mobile.dansales.model;

/**
 * It provides information to configure pagination on AndroidTreeView.
 */
public class TreeNodePageConfiguration {

    private int pageSize;
    private int levelUsage;
    private String label;

    /**
     * It provides information to configure pagination on AndroidTreeView.<br>
     * Use levelUsage -1 to paginate all levels.
     * @param levelUsage level usage e.g. paginate only level 1, level 2, etc. or all levels
     */
    public TreeNodePageConfiguration(int pageSize, int levelUsage, String label) {
        this.pageSize = pageSize;
        this.levelUsage = levelUsage;
        this.label = label;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getLevelUsage() {
        return levelUsage;
    }

    public String getLabel() {
        return label;
    }
}

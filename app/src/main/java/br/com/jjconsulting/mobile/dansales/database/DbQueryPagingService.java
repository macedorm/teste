package br.com.jjconsulting.mobile.dansales.database;

public class DbQueryPagingService {

    private int pageSize;
    private int recordCounter;

    public DbQueryPagingService(int pageSize) {
        this.pageSize = pageSize;
        this.recordCounter = 0;
    }

    public DbQueryPagingService(int pageSize, int recordCounter) {
        this.pageSize = pageSize;
        this.recordCounter = recordCounter;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getRecordCounter() {
        return recordCounter;
    }

    /**
     * Go to the next page, adding the page size to the record counter.
     */
    public void goToNextPage() {
        recordCounter += pageSize;
    }

    /**
     * Go to the first page, resetting the record counter.
     */
    public void goToFirstPage() {
        recordCounter = 0;
    }
}

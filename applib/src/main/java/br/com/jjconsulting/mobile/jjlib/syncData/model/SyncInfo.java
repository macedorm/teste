package br.com.jjconsulting.mobile.jjlib.syncData.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by jjconsulting on 01/03/2018.
 */

public class SyncInfo {


    private String serverDate;
    private List<SyncInfoElement> listElement;

    public String getServerDate() {
        return serverDate;
    }

    public void setServerDate(String serverDate) {
        this.serverDate = serverDate;
    }

    public List<SyncInfoElement> getListElement() {
        return listElement;
    }

    public void setListElement(List<SyncInfoElement> listElement) {
        this.listElement = listElement;
    }


    public class SyncInfoElement {

        private String name;
        private Integer recordSize;
        @SerializedName("totPerPage")
        private Integer totPerPage;
        private String lastSync;
        private int size;
        private int totalPage;

        public String getLastSync() {
            return lastSync;
        }

        public void setLastSync(String lastSync) {
            this.lastSync = lastSync;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getRecordSize() {
            return recordSize;
        }

        public void setRecordSize(Integer recordSize) {
            this.recordSize = recordSize;
        }

        public Integer getTotPerPage() {
            return totPerPage;
        }

        public void setTotPerPage(Integer totPerPage) {
            this.totPerPage = totPerPage;
        }


        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

    }
}

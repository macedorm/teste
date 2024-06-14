package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jjconsulting on 01/03/2018.
 */

public class SyncInfo {

    @SerializedName("ResultProcess")
    private RetProcess resultProcess;

    @SerializedName("ServerDate")
    private String serverDate;

    @SerializedName("listElement")
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

    public RetProcess getResultProcess() {
        return resultProcess;
    }

    public void setResultProcess(RetProcess resultProcess) {
        this.resultProcess = resultProcess;
    }

    public class SyncInfoElement {

        @SerializedName("Name")
        private String name;
        @SerializedName("RecordSize")
        private Integer recordSize;
        @SerializedName("TotPerPage")
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

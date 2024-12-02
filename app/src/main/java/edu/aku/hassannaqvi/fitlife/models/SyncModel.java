package edu.aku.hassannaqvi.fitlife.models;


import static edu.aku.hassannaqvi.fitlife.core.MainApp._EMPTY_;

public class SyncModel {
    Boolean isUnlocked = false;
    String tableTitle;
    String tableName;
    String status;
    int statusID;
    String message;
    String filter = null;
    String select = null;
    String info;

    public SyncModel(String tableName) {

        this.tableName = tableName;
        this.tableTitle = tableName.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");
        this.status = _EMPTY_;
        this.statusID = 0;
        this.message = _EMPTY_;


    }

    public SyncModel(String tableName, String select) {

        this.tableName = tableName;
        this.tableTitle = tableName.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");
        this.status = _EMPTY_;
        this.statusID = 0;
        this.message = _EMPTY_;
        this.select = select;
    }

    public SyncModel(String tableName, String select, String filter) {

        this.tableName = tableName;
        this.tableTitle = tableName.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");
        this.status = _EMPTY_;
        this.statusID = 0;
        this.message = _EMPTY_;
        this.select = select;
        this.filter = filter;

    }

    public SyncModel(String tableName, Boolean isUnlocked) {

        this.tableName = tableName;
        this.tableTitle = tableName.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");
        this.status = _EMPTY_;
        this.statusID = 0;
        this.message = _EMPTY_;
        this.isUnlocked = isUnlocked;
    }

    public String getTableTitle() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    public String getTableName() {
        return tableName;
    }

    public void settableName(String tableName) {
        this.tableName = tableName;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public int getstatusID() {
        return statusID;
    }

    public void setstatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public String getFilter() {

        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }
}

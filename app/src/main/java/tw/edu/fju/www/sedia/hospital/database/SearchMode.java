package tw.edu.fju.www.sedia.hospital.database;

public enum SearchMode {
    FIND_BY_ID("find by hospital id"),
    FIND_BY_ADDRESS("find by hospital address");

    String searchModeValue;

    SearchMode(String searchModeValue) {
        this.searchModeValue = searchModeValue;
    }
}

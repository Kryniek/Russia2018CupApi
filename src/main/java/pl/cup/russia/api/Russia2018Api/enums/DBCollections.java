package pl.cup.russia.api.Russia2018Api.enums;

public enum DBCollections {

    LEAGUES("leagues");

    private String value;

    DBCollections(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
package com.code10.xml.model.dto;

public class PaperResponse {

    private String id;

    private String title;

    private boolean revisionRequired = false;

    public PaperResponse() {
    }

    public PaperResponse(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public PaperResponse(String id, String title, boolean revisionRequired) {
        this.id = id;
        this.title = title;
        this.revisionRequired = revisionRequired;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRevisionRequired() {
        return revisionRequired;
    }

    public void setRevisionRequired(boolean revisionRequired) {
        this.revisionRequired = revisionRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaperResponse that = (PaperResponse) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

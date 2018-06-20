package de.tsystems.mms.apm.performancesignature.viewer.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class BuildData {

    @SerializedName("artifacts")
    @Expose
    private List<Artifact> artifacts = new ArrayList<>();

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("artifacts", artifacts).toString();
    }
}

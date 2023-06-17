package com.leosam.tvbox.mv.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VodClass {

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("type_name")
    private String typeName;

    public String getTypeId() {
        return typeId;
    }

    public VodClass setTypeId(String typeId) {
        this.typeId = typeId;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public VodClass setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }
}

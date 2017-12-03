package com.maciejak.myplaces.api.dto.response.error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "codes",
        "arguments",
        "defaultMessage",
        "code"
})
public class Argument {

    @JsonProperty("codes")
    private List<String> codes = null;
    @JsonProperty("arguments")
    private Object arguments;
    @JsonProperty("defaultMessage")
    private String defaultMessage;
    @JsonProperty("code")
    private String code;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("codes")
    public List<String> getCodes() {
        return codes;
    }

    @JsonProperty("codes")
    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    @JsonProperty("arguments")
    public Object getArguments() {
        return arguments;
    }

    @JsonProperty("arguments")
    public void setArguments(Object arguments) {
        this.arguments = arguments;
    }

    @JsonProperty("defaultMessage")
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @JsonProperty("defaultMessage")
    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

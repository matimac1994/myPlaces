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
        "objectName",
        "field",
        "rejectedValue",
        "bindingFailure",
        "code"
})
public class Error {

    @JsonProperty("codes")
    private List<String> codes = null;
    @JsonProperty("arguments")
    private List<Argument> arguments = null;
    @JsonProperty("defaultMessage")
    private String defaultMessage;
    @JsonProperty("objectName")
    private String objectName;
    @JsonProperty("field")
    private String field;
    @JsonProperty("rejectedValue")
    private Object rejectedValue;
    @JsonProperty("bindingFailure")
    private Boolean bindingFailure;
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
    public List<Argument> getArguments() {
        return arguments;
    }

    @JsonProperty("arguments")
    public void setArguments(List<Argument> arguments) {
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

    @JsonProperty("objectName")
    public String getObjectName() {
        return objectName;
    }

    @JsonProperty("objectName")
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @JsonProperty("field")
    public String getField() {
        return field;
    }

    @JsonProperty("field")
    public void setField(String field) {
        this.field = field;
    }

    @JsonProperty("rejectedValue")
    public Object getRejectedValue() {
        return rejectedValue;
    }

    @JsonProperty("rejectedValue")
    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    @JsonProperty("bindingFailure")
    public Boolean getBindingFailure() {
        return bindingFailure;
    }

    @JsonProperty("bindingFailure")
    public void setBindingFailure(Boolean bindingFailure) {
        this.bindingFailure = bindingFailure;
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

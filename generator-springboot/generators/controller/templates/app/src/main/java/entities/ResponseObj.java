package <%= packageName %>.entities;

import java.util.Objects;

public class ResponseObj {
    private Boolean success;
    private String message;
    private Object data;

    public ResponseObj() {
    }

    public ResponseObj(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Boolean isSuccess() {
        return this.success;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseObj success(Boolean success) {
        setSuccess(success);
        return this;
    }

    public ResponseObj message(String message) {
        setMessage(message);
        return this;
    }

    public ResponseObj data(Object data) {
        setData(data);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ResponseObj)) {
            return false;
        }
        ResponseObj responseObj = (ResponseObj) o;
        return Objects.equals(success, responseObj.success) && Objects.equals(message, responseObj.message) && Objects.equals(data, responseObj.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, data);
    }

    @Override
    public String toString() {
        return "{" +
            " success='" + isSuccess() + "'" +
            ", message='" + getMessage() + "'" +
            ", data='" + getData() + "'" +
            "}";
    }

}

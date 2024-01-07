package pl.mimuw.worker.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum MonitorResult {
    SUCCESS("SUC"),
    FAILURE("FAIL"),
    ERROR_TIMEOUT("ERR_TO"),
    ERROR_DNS("ERR-DN"),
    ERROR_NO_RESPONSE("ERR-NR");

    private final String code;

    public static MonitorResult fromCode(String code) {
        for (MonitorResult result : MonitorResult.values()) {
            if (result.code.equals(code)) {
                return result;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

    public static MonitorResult fromStatusCode(final HttpStatusCode statusCode) {
        if (statusCode.is2xxSuccessful() || statusCode.is3xxRedirection()) {
            return MonitorResult.SUCCESS;
        } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
            return MonitorResult.FAILURE;
        } else {
            return MonitorResult.ERROR_NO_RESPONSE;
        }
    }
}

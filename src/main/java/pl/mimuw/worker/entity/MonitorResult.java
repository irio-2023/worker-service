package pl.mimuw.worker.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}

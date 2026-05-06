package app.event.exceptions;


import event.app.dto.ErrorResponse;
import event.app.dto.ErrorDetail;


import java.util.List;

public final class Errors {
    private Errors() {}

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, ErrorResponse.LevelEnum.ERROR_VALUE_ERROR_, message);
    }

    public static ErrorResponse of(String code, String message, List<ErrorDetail> details) {
        ErrorResponse r = new ErrorResponse(code, ErrorResponse.LevelEnum.ERROR_VALUE_ERROR_, message);
        if (details != null) {
            List<ErrorDetail> convertedDetails = details.stream()
                    .map(inner -> {
                        ErrorDetail detail = new ErrorDetail();
                        detail.setMessage(inner.getMessage());
                        return detail;
                    })
                    .toList();
            r.setDetails(convertedDetails);
        }
        return r;
    }
}


package pl.mimuw.worker.entity;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class MonitorResultToStringConverter implements Converter<MonitorResult, String> {

    @Override
    public String convert(MonitorResult source) {
        return source.getCode();
    }
}

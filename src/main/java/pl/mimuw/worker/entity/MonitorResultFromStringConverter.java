package pl.mimuw.worker.entity;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class MonitorResultFromStringConverter implements Converter<String, MonitorResult> {

    @Override
    public MonitorResult convert(String source) {
        return MonitorResult.fromCode(source);
    }
}
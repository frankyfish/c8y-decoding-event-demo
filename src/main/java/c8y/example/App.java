package c8y.example;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.customdecoders.api.model.DecoderInputData;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementDto;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementValueDto;
import com.cumulocity.rest.representation.event.EventRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
@MicroserviceApplication
@RestController
@RequestMapping(value = "/decode")
@Slf4j
public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DecoderResult decodeThings(@RequestBody DecoderInputData inputData) {
        log.info("Starting fake decoding, telegram: {}", inputData);
        DecoderResult decoderResult = new DecoderResult();

        List<MeasurementValueDto> measurementValueDtos = new ArrayList<>();
        MeasurementValueDto measurementValueDto = new MeasurementValueDto();
        measurementValueDto.setValue(BigDecimal.valueOf(42L));
        measurementValueDto.setSeriesName("UltimateAnswer");
        measurementValueDto.setUnit("unknown");
        measurementValueDtos.add(measurementValueDto);

        MeasurementDto measurementDto = new MeasurementDto();
        measurementDto.setType("c8y_example_lwm2m_decoder_binaryValues_byteIndex"); // taken from: https://bitbucket.org/m2m/cumulocity-examples/src/2b0d829e40caf729a1930065d5a9b39a300e04c9/sample-lwm2m-custom-decoder/src/main/java/c8y/example/bytesequencedecoder/ByteSequenceDecoderService.java#lines-57
        measurementDto.setTime(new DateTime());
        measurementDto.setValues(measurementValueDtos);
        measurementDto.setSeries("binaryValueSeries");
        log.info("measurements generated");

        decoderResult.addEvent(generateEvent("non-internal"), false);
        decoderResult.addEvent(generateEvent("internal"), true);
        decoderResult.addMeasurement(measurementDto);
        return decoderResult;
    }

    private EventRepresentation generateEvent(String val) {
        log.info("Generating event");
        EventRepresentation eventRepresentation = new EventRepresentation();
        eventRepresentation.setType("c8y_LocationUpdate");// event-type taken from https://bitbucket.org/m2m/cumulocity-examples/src/2b0d829e40caf729a1930065d5a9b39a300e04c9/microservices/iptracker-microservice/src/main/java/c8y/example/App.java#lines-125
        eventRepresentation.setDateTime(DateTime.now());
        eventRepresentation.setText(String.format("Generated event: Ultimate answer is found! %s", val));
        return eventRepresentation;
    }

}

package uk.gov.moj.sdt.cmc.consumers.request.judgement;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FullPaymentType {

    @JsonFormat(pattern="yyyy-MM-dd")
    protected Date fullByDate;
}

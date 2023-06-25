package uk.gov.moj.sdt.cmc.consumers.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "results")
@XmlAccessorType(XmlAccessType.FIELD)
public class McolDefenceDetailTypes {

    @XmlElement(name = "mcolDefenceDetail")
    protected List<McolDefenceDetailType> mcolDefenceDetailTypeList;

    public void setMcolDefenceDetailTypeList(List<McolDefenceDetailType> mcolDefenceDetailTypeList) {
        this.mcolDefenceDetailTypeList = mcolDefenceDetailTypeList;
    }

}

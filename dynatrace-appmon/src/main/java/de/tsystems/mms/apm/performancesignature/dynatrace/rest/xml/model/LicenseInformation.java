/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "licenseinformation")
public class LicenseInformation {
    private static final String PRODUCTION_EDITION = "Production Edition";
    private static final String PRE_PRODUCTION_EDITION = "Pre-Production Edition";

    @XmlElement
    private String expiredate;
    @XmlElement
    private String nextvolumerenewaldate;
    @XmlElement
    private String licensedto;
    @XmlElement
    private String usedvolumepercentage;
    @XmlElement
    private String maximaluemtransactions;
    @XmlElement
    private String licensenumber;
    @XmlElement
    private String validfrom;
    @XmlElement
    private String licenseedition;
    @XmlElement
    private String currentuemtransactions;

    public boolean isProductionLicence() {
        return licenseedition.equals(PRODUCTION_EDITION);
    }

    public boolean isPreProductionLicence() {
        return licenseedition.equals(PRE_PRODUCTION_EDITION);
    }

    public String getLicensedto() {
        return licensedto;
    }

    public String getUsedvolumepercentage() {
        return usedvolumepercentage;
    }

    public String getMaximaluemtransactions() {
        return maximaluemtransactions;
    }

    public String getLicensenumber() {
        return licensenumber;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public String getLicenseedition() {
        return licenseedition;
    }

    public String getNextvolumerenewaldate() {
        return nextvolumerenewaldate;
    }

    public String getCurrentuemtransactions() {
        return currentuemtransactions;
    }

    public String getValidfrom() {
        return validfrom;
    }
}

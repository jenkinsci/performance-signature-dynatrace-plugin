/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.BaseReference;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;

/**
 * SystemProfileConfiguration
 */

public class SystemProfileConfiguration extends BaseReference {
    @SerializedName("isactive")
    private Boolean isactive;

    /**
     * Get isactive
     *
     * @return isactive
     **/

    public Boolean getIsactive() {
        return isactive;
    }

    @Override
    public String toString() {
        return "class SystemProfileConfiguration {\n" +
                "    id: " + PerfSigUIUtils.toIndentedString(super.getId()) + "\n" +
                "    href: " + PerfSigUIUtils.toIndentedString(super.getHref()) + "\n" +
                "    isactive: " + PerfSigUIUtils.toIndentedString(isactive) + "\n" +
                "}";
    }
}

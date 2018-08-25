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

package de.tsystems.mms.apm.performancesignature.ui.model;

import java.io.Serializable;
import java.util.Comparator;

public class JSONDashletComparator implements Comparator<JSONDashlet>, Serializable {
    @Override
    public int compare(final JSONDashlet a, final JSONDashlet b) {
        if (a.getRow() > b.getRow() || a.getRow() == b.getRow() && a.getCol() > b.getCol()) {
            return 1;
        } else if (a.equals(b)) {
            return 0;
        }
        return -1;
    }
}

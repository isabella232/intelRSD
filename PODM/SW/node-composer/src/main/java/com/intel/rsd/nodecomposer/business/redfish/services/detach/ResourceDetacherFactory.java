/*
 * Copyright (c) 2017-2019 Intel Corporation
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

package com.intel.rsd.nodecomposer.business.redfish.services.detach;

import com.intel.rsd.nodecomposer.business.RequestValidationException;
import com.intel.rsd.nodecomposer.business.services.redfish.odataid.ODataId;
import com.intel.rsd.nodecomposer.types.AttachableType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.intel.rsd.nodecomposer.business.Violations.createWithViolations;
import static com.intel.rsd.nodecomposer.business.redfish.services.helpers.AttachableTypeFinder.getAttachableTypeByODataId;
import static com.intel.rsd.nodecomposer.utils.Contracts.requiresNonNull;
import static java.lang.String.format;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class ResourceDetacherFactory {
    private final Map<String, Detacher> resourceDetacherMapping;

    @Autowired
    public ResourceDetacherFactory(Map<String, Detacher> resourceDetacherMapping) {
        this.resourceDetacherMapping = resourceDetacherMapping;
    }

    public Detacher getDetacherForResource(ODataId oDataId) throws RequestValidationException {
        requiresNonNull(oDataId, "oDataId");

        AttachableType attachableType = getAttachableTypeByODataId(oDataId);

        return getDetacherForResource(attachableType);
    }

    private Detacher getDetacherForResource(AttachableType attachableType) throws RequestValidationException {
        requiresNonNull(attachableType, "attachableType");
        Detacher detacher = resourceDetacherMapping.get(attachableType.toString());

        if (detacher == null) {
            throw new RequestValidationException(createWithViolations(format("Detach action is not supported for %s.", attachableType)));
        }

        return detacher;
    }
}

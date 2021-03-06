/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

///**
// *  * #%L
// *  *
// *  * %%
// *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
// *  * %%
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *      http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *  * #L%
// */
//
//package org.hspconsortium.platform.api.fhir.tenantid;
//
//import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
//import org.hspconsortium.platform.api.fhir.MultitenantDatabaseProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class HspcCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
//
//    @Autowired
//    private MultitenantDatabaseProperties multitenantDatabaseProperties;
//
//    @Override
//    public String resolveCurrentTenantIdentifier() {
//
//        if (TenantContextHolder.getTenant() != null) {
//            String tenantId = TenantContextHolder.getTenant();
//            if (tenantId != null) {
//                return tenantId;
//            }
//        }
//
//        return multitenantDatabaseProperties.getDefaultTenantId();
//    }
//
//    @Override
//    public boolean validateExistingCurrentSessions() {
//        return true;
//    }
//}

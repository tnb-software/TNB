/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.tnb.product.ck.utils;

/**
 * Common Camel-K settings class.
 *
 */
public final class CamelKSettings {

    public static final String API_VERSION_DEFAULT = "v1";

    public static final String KAMELET_API_VERSION_DEFAULT = "v1";

    private CamelKSettings() {
        // prevent instantiation of utility class
    }

    /**
     * Api version for current Camel-K installation.
     *
     * @return
     */
    public static String getApiVersion() {
        return API_VERSION_DEFAULT;
    }

    /**
     * Api version for current Kamelet specification.
     *
     * @return
     */
    public static String getKameletApiVersion() {
        return KAMELET_API_VERSION_DEFAULT;
    }
}

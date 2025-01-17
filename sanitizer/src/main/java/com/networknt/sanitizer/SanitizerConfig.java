/*
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.sanitizer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.config.Config;
import com.networknt.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Sanitizer configuration class. The new config class will be backward compatible
 * to the old version of the config file generated by the light-codegen.
 *
 * @author Steve Hu
 */
public class SanitizerConfig {
    public static Logger logger = LoggerFactory.getLogger(SanitizerConfig.class);

    public static final String CONFIG_NAME = "sanitizer";

    // the default encoder value for the old version of config file.
    public static final String DEFAULT_ENCODER = "javascript-source";
    public static final String BODY_ENCODER = "bodyEncoder";
    public static final String HEADER_ENCODER = "headerEncoder";
    public static final String BODY_ATTRIBUTES_TO_ENCODE = "bodyAttributesToEncode";
    public static final String BODY_ATTRIBUTES_TO_IGNORE = "bodyAttributesToIgnore";
    public static final String HEADER_ATTRIBUTES_TO_ENCODE = "headerAttributesToEncode";
    public static final String HEADER_ATTRIBUTES_TO_IGNORE = "headerAttributesToIgnore";


    private boolean enabled;
    private boolean bodyEnabled;
    private String bodyEncoder;
    private List<String> bodyAttributesToIgnore;
    private List<String> bodyAttributesToEncode;

    private boolean headerEnabled;
    private String headerEncoder;
    private List<String> headerAttributesToIgnore;
    private List<String> headerAttributesToEncode;

    private final Map<String, Object> mappedConfig;

    private SanitizerConfig(String configName) {
        mappedConfig = Config.getInstance().getJsonMapConfig(configName);
        setConfigList();
        setConfigData();
    }

    public static SanitizerConfig load() {
        return new SanitizerConfig(CONFIG_NAME);
    }

    @Deprecated
    public static SanitizerConfig load(String configName) {
        return new SanitizerConfig(configName);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBodyEnabled() {
        return bodyEnabled;
    }

    public void setBodyEnabled(boolean bodyEnabled) {
        this.bodyEnabled = bodyEnabled;
    }

    public String getBodyEncoder() {
        return bodyEncoder;
    }

    public void setBodyEncoder(String bodyEncoder) {
        this.bodyEncoder = bodyEncoder;
    }

    public List<String> getBodyAttributesToIgnore() {
        return bodyAttributesToIgnore;
    }

    public void setBodyAttributesToIgnore(List<String> bodyAttributesToIgnore) {
        this.bodyAttributesToIgnore = bodyAttributesToIgnore;
    }

    public List<String> getBodyAttributesToEncode() {
        return bodyAttributesToEncode;
    }

    public void setBodyAttributesToEncode(List<String> bodyAttributesToEncode) {
        this.bodyAttributesToEncode = bodyAttributesToEncode;
    }

    public boolean isHeaderEnabled() {
        return headerEnabled;
    }

    public void setHeaderEnabled(boolean headerEnabled) {
        this.headerEnabled = headerEnabled;
    }

    public String getHeaderEncoder() {
        return headerEncoder;
    }

    public void setHeaderEncoder(String headerEncoder) {
        this.headerEncoder = headerEncoder;
    }

    public List<String> getHeaderAttributesToIgnore() {
        return headerAttributesToIgnore;
    }

    public void setHeaderAttributesToIgnore(List<String> headerAttributesToIgnore) {
        this.headerAttributesToIgnore = headerAttributesToIgnore;
    }

    public List<String> getHeaderAttributesToEncode() {
        return headerAttributesToEncode;
    }

    public void setHeaderAttributesToEncode(List<String> headerAttributesToEncode) {
        this.headerAttributesToEncode = headerAttributesToEncode;
    }

    public void setConfigData() {
        Object object = mappedConfig.get("enabled");
        if(object != null) {
            if(object instanceof String) {
                enabled = Boolean.parseBoolean((String)object);
            } else if (object instanceof Boolean) {
                enabled = (Boolean) object;
            } else {
                throw new ConfigException("enabled must be a boolean value.");
            }
        }

        object = mappedConfig.get("bodyEnabled");
        if(object != null) {
            if(object instanceof String) {
                bodyEnabled = Boolean.parseBoolean((String)object);
            } else if (object instanceof Boolean) {
                bodyEnabled = (Boolean) object;
            } else {
                object = mappedConfig.get("sanitizeBody");
                if(object != null) {
                    if(object instanceof String) {
                        bodyEnabled = Boolean.parseBoolean((String)object);
                    } else if (object instanceof Boolean) {
                        bodyEnabled = (Boolean) object;
                    } else {
                        throw new ConfigException("sanitizeBody must be a boolean value.");
                    }
                }
            }
        }
        object = mappedConfig.get("headerEnabled");
        if(object != null) {
            if(object instanceof String) {
                headerEnabled = Boolean.parseBoolean((String)object);
            } else if (object instanceof Boolean) {
                headerEnabled = (Boolean) object;
            } else {
                object = mappedConfig.get("sanitizeHeader");
                if(object != null) {
                    if(object instanceof String) {
                        headerEnabled = Boolean.parseBoolean((String)object);
                    } else if (object instanceof Boolean) {
                        headerEnabled = (Boolean) object;
                    } else {
                        throw new ConfigException("sanitizeHeader must be a boolean value.");
                    }
                }
            }
        }
        object = mappedConfig.get(BODY_ENCODER);
        if(object != null ) {
            bodyEncoder = (String)object;
        } else {
            bodyEncoder = DEFAULT_ENCODER;
        }

        object = mappedConfig.get(HEADER_ENCODER);
        if(object != null ) {
            headerEncoder = (String)object;
        } else {
            headerEncoder = DEFAULT_ENCODER;
        }

    }

    private void setConfigList() {

        if(mappedConfig.get(BODY_ATTRIBUTES_TO_ENCODE) != null) {
            Object object = mappedConfig.get(BODY_ATTRIBUTES_TO_ENCODE);
            if(object instanceof String) {
                String s = (String)object;
                s = s.trim();
                if(logger.isTraceEnabled()) logger.trace("bodyAttributesToEncode = " + s);
                if(s.startsWith("[")) {
                    // this is a JSON string, and we need to parse it.
                    try {
                        bodyAttributesToEncode = Config.getInstance().getMapper().readValue(s, new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        throw new ConfigException("could not parse the bodyAttributesToEncode json with a list of strings.");
                    }
                } else {
                    // this is a comma separated string.
                    bodyAttributesToEncode = Arrays.asList(s.split("\\s*,\\s*"));
                }
            } else if (object instanceof List) {
                bodyAttributesToEncode = (List<String>)object;
            } else {
                throw new ConfigException("bodyAttributesToEncode list is missing or wrong type.");
            }
        }

        if(mappedConfig.get(BODY_ATTRIBUTES_TO_IGNORE) != null) {
            Object object = mappedConfig.get(BODY_ATTRIBUTES_TO_IGNORE);
            if(object instanceof String) {
                String s = (String)object;
                s = s.trim();
                if(logger.isTraceEnabled()) logger.trace("bodyAttributesToIgnore = " + s);
                if(s.startsWith("[")) {
                    // this is a JSON string, and we need to parse it.
                    try {
                        bodyAttributesToIgnore = Config.getInstance().getMapper().readValue(s, new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        throw new ConfigException("could not parse the bodyAttributesToIgnore json with a list of strings.");
                    }
                } else {
                    // this is a comma separated string.
                    bodyAttributesToIgnore = Arrays.asList(s.split("\\s*,\\s*"));
                }
            } else if (object instanceof List) {
                bodyAttributesToIgnore = (List<String>)object;
            } else {
                throw new ConfigException("bodyAttributesToIgnore list is missing or wrong type.");
            }
        }

        if(mappedConfig.get(HEADER_ATTRIBUTES_TO_ENCODE) != null) {
            Object object = mappedConfig.get(HEADER_ATTRIBUTES_TO_ENCODE);
            if(object instanceof String) {
                String s = (String)object;
                s = s.trim();
                if(logger.isTraceEnabled()) logger.trace("headerAttributesToEncode = " + s);
                if(s.startsWith("[")) {
                    // this is a JSON string, and we need to parse it.
                    try {
                        headerAttributesToEncode = Config.getInstance().getMapper().readValue(s, new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        throw new ConfigException("could not parse the headerAttributesToEncode json with a list of strings.");
                    }
                } else {
                    // this is a comma separated string.
                    headerAttributesToEncode = Arrays.asList(s.split("\\s*,\\s*"));
                }
            } else if (object instanceof List) {
                headerAttributesToEncode = (List<String>)object;
            } else {
                throw new ConfigException("headerAttributesToEncode list is missing or wrong type.");
            }
        }

        if(mappedConfig.get(HEADER_ATTRIBUTES_TO_IGNORE) != null) {
            Object object = mappedConfig.get(HEADER_ATTRIBUTES_TO_IGNORE);
            if(object instanceof String) {
                String s = (String)object;
                s = s.trim();
                if(logger.isTraceEnabled()) logger.trace("headerAttributesToIgnore = " + s);
                if(s.startsWith("[")) {
                    // this is a JSON string, and we need to parse it.
                    try {
                        headerAttributesToIgnore = Config.getInstance().getMapper().readValue(s, new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        throw new ConfigException("could not parse the headerAttributesToIgnore json with a list of strings.");
                    }
                } else {
                    // this is a comma separated string.
                    headerAttributesToIgnore = Arrays.asList(s.split("\\s*,\\s*"));
                }
            } else if (object instanceof List) {
                headerAttributesToIgnore = (List<String>)object;
            } else {
                throw new ConfigException("headerAttributesToIgnore list is missing or wrong type.");
            }
        }

    }

}

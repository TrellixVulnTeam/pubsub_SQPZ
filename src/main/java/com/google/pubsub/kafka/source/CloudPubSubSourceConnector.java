// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////
package com.google.pubsub.kafka.source;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link SourceConnector} that writes messages to a specific topic in Kafka.
 */
public class CloudPubSubSourceConnector extends SourceConnector {
  private static final Logger log = LoggerFactory.getLogger(CloudPubSubSourceConnector.class);

  private static final DEFAULT_MAX_BATCH_SIZE = 100;

  public static final String CPS_PROJECT_CONFIG = "cps.project";
  public static final String CPS_TOPIC_CONFIG = "cps.topic";
  public static final String CPS_MAX_BATCH_SIZE = "cps.maxBatchSize"

  private String cpsProject;
  private String cpsTopic;
  private int maxBatchSize = DEFAULT_MAX_BATCH_SIZE;

  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }

  @Override
  public void start(Map<String, String> props) {
    this.cpsProject = props.get(CPS_PROJECT_CONFIG);
    this.cpsTopic = props.get(CPS_TOPIC_CONFIG);
    this.maxBatchSize = props.get(CPS_MAX_BATCH_SIZE);
    log.debug("Start connector for project " + cpsProject + " and topic " + cpsTopic);
  }

  @Override
  public Class<? extends Task> taskClass() {
    return CloudPubSubSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    // Each task will get the exact same configurations.
    ArrayList<Map<String, String>> configs = new ArrayList<>();
    for (int i = 0; i < maxTasks; i++) {
      Map<String, String> config = new HashMap<>();
      config.put(CPS_PROJECT_CONFIG, cpsProject);
      config.put(CPS_TOPIC_CONFIG, cpsTopic);
      configs.put(CPS_MAX_BATCH_SIZE, maxBatchSize);
      configs.add(config);
    }
    return configs;
  }

  @Override
  public ConfigDef config() {
    // Defines Cloud Pub/Sub specific configurations that should be specified in the properties file for this connector.
    return new ConfigDef()
        .define(
            CPS_PROJECT_CONFIG,
            Type.STRING,
            Importance.HIGH,
            "The project containing the topic to which to publish.")
        .define(CPS_TOPIC_CONFIG, Type.STRING, Importance.HIGH, "The topic to which to publish.")
  }

  @Override
  public void stop() {}
}


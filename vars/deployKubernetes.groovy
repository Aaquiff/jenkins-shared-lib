#!/usr/bin/env groovy
/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

def call(Map config) {
  dir("${config.KUBERNETES_DIR}") {
    sh """
      sed -i 's|<IMAGE>|${config.IMAGE_NAME}|' values-${config.ENVIRONMENT}.yaml
      sed -i 's|<ENVIRONMENT>|${config.ENVIRONMENT}|' values-${config.ENVIRONMENT}.yaml
      sed -i 's|<NAMESPACE>|wso2-${config.ENVIRONMENT}|' values-${config.ENVIRONMENT}.yaml
      sed -i 's|<TEST_ENDPOINT>|${config.TEST_ENDPOINT}|' values-${config.ENVIRONMENT}.yaml
      helm upgrade -f values-${config.ENVIRONMENT}.yaml wso2ei-${config.ENVIRONMENT} . --namespace wso2-${config.ENVIRONMENT} --install
    """
  }
}

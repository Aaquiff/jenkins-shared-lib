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

String call(Map config) {
  
  def WUM = "wum"
  def MV="mv"
  def UNZIP="unzip"
  def RM ="rm"

  def PRODUCT="${config.PRODUCT}"
  def PRODUCT_VERSION="${config.VERSION}"
  def CHANNEL="full"
  def WUM_PRODUCT_HOME="${config.WUM_PRODUCT_HOME}"
  def PACK_DEST="${config.PACK_DEST}"

  def FAILED_WUM_UPDATE=10
  def FAILED_WUM_ADD=11
  def FAILED_INPLACE_UPDATES=12
  def FAILED_PUPPET_APPLY=13
  def FAILED_TO_MOVE_WUMMED_PRODUCT=14
  def FAILED_UNZIP=15
  def FAILED_RM_UNZIP=16

  sh """
    ${WUM} add ${PRODUCT}-${PRODUCT_VERSION} -y &>> wum.log

    if ${WUM} describe ${PRODUCT}-${PRODUCT_VERSION}; 
    then 
      echo "${PRODUCT}-${PRODUCT_VERSION} already exists"; 
    else 
      wum ${WUM} add ${PRODUCT}-${PRODUCT_VERSION} 
    fi;
    
    echo "Updating ${PRODUCT}-${PRODUCT_VERSION}"
    ${WUM} update ${PRODUCT}-${PRODUCT_VERSION} ${CHANNEL}
  """

  def timestamp = sh (
    script: "${WUM} describe ${PRODUCT}-${PRODUCT_VERSION}  | grep Filename: | awk -F'[+]' '{print $2}' | awk -F'.' '{print $1}'",
    returnStdout: true).trim()

  sh """
    ${MV} ${WUM_PRODUCT_HOME}/${PRODUCT}/${PRODUCT_VERSION}/${CHANNEL}/${PRODUCT}-${PRODUCT_VERSION}*.zip ${PACK_DEST}/${PRODUCT}-${PRODUCT_VERSION}.zip
    ${UNZIP} -o -q ${PACK_DEST}/${PRODUCT}-${PRODUCT_VERSION}.zip -d ${PACK_DEST}/
    ${RM} ${PACK_DEST}/${PRODUCT}-${PRODUCT_VERSION}.zip
  """

  return ${timestamp};
}

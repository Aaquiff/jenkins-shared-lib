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
  
  def WUM = "wum"
  def MV="mv"
  def UNZIP="unzip"
  def RM ="rm"

  def PRODUCT="${config.PRODUCT}"
  def PRODUCT_VERSION="${config.VERSION}"
  def CHANNEL="full"
  def WUM_PRODUCT_HOME="${config.WUM_PRODUCT_HOME}"
  def PACK_DEST="${config.PACK_DEST}"

  sh """
    ${WUM} init -u "${config.WSO2_USERNAME}" -p "${config.WSO2_PASSWORD}"
    if ${WUM} list | grep "${PRODUCT}-${PRODUCT_VERSION}"; 
    then 
        echo "${PRODUCT}-${PRODUCT_VERSION} already exists"; 
    else 
        ${WUM} add "${PRODUCT}-${PRODUCT_VERSION}" -y
        echo "added ${PRODUCT}-${PRODUCT_VERSION}"; 
    fi;
    
    echo "Get latest updates for the product - ${PRODUCT}-${PRODUCT_VERSION}..." &>> wum.log
    ${WUM} update ${PRODUCT}-${PRODUCT_VERSION} ${CHANNEL} &>> wum.log
    if [ \$? -eq 0 ] ; then
      echo "${PRODUCT}-${PRODUCT_VERSION} successfully updated..." &>> wum.log
    else
      if [ \$? -eq 1 ] ; then
        exit 1
      fi
    fi

    echo "Moving the WUM updated product..." &>> wum.log
    ${MV} ${WUM_PRODUCT_HOME}/${PRODUCT}/${PRODUCT_VERSION}/${CHANNEL}/${PRODUCT}-${PRODUCT_VERSION}*.zip ${PACK_DEST}/${PRODUCT}-${PRODUCT_VERSION}.zip
    if [ \$? -ne 0 ] ; then
      echo "Failed to move the WUM updated product from ${WUM_PRODUCT_HOME}/${PRODUCT}/${PRODUCT_VERSION}/${CHANNEL} to ${PACK_DEST}..."
      exit 1
    fi

    echo "Unzip the WUM updated product..." &>> wum.log
    ${UNZIP} -o -q ${PACK_DEST}/${PRODUCT}-${PRODUCT_VERSION}.zip -d ${PACK_DEST}/
    if [ \$? -ne 0 ] ; then
      echo "Failed to unzip the WUM updated product ${PRODUCT}-${PRODUCT_VERSION}..."
      exit 1
    fi

    echo "Remove the zipped product..." &>> wum.log
    ${RM} ${PACK_DEST}/${PRODUCT}-${PRODUCT_VERSION}.zip
    if [ \$? -ne 0 ] ; then
      echo "Failed to remove the zipped product ${PRODUCT}-${PRODUCT_VERSION}..."
      exit 1
    fi
  """

}

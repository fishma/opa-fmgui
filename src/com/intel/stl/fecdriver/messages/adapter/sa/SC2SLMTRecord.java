/**
 * INTEL CONFIDENTIAL
 * Copyright (c) 2014 Intel Corporation All Rights Reserved.
 * The source code contained or described herein and all documents related to the source code ("Material")
 * are owned by Intel Corporation or its suppliers or licensors. Title to the Material remains with Intel
 * Corporation or its suppliers and licensors. The Material contains trade secrets and proprietary and
 * confidential information of Intel or its suppliers and licensors. The Material is protected by
 * worldwide copyright and trade secret laws and treaty provisions. No part of the Material may be used,
 * copied, reproduced, modified, published, uploaded, posted, transmitted, distributed, or disclosed in
 * any way without Intel's prior express written permission. No license under any patent, copyright,
 * trade secret or other intellectual property right is granted to or conferred upon you by disclosure
 * or delivery of the Materials, either expressly, by implication, inducement, estoppel or otherwise.
 * Any license under such intellectual property rights must be express and approved by Intel in writing.
 */
package com.intel.stl.fecdriver.messages.adapter.sa;

import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.api.subnet.SC2SLMTRecordBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa.h.1.91<br>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_types.h.1.20
 * 
 * <pre>
 * 
 * 
 * SC2SLMappingTableRecord
 * 
 * STL Differences:
 *      New for STL.
 * 
 * typedef struct {
 *     struct {
 *         uint32  LID;    
 *         uint16  Reserved;               
 *     } PACK_SUFFIX RID;
 *    
 *     uint16      Reserved2;
 *    
 *     STL_SL      SCSLMap[STL_MAX_SLS];   
 *    
 * } PACK_SUFFIX STL_SC2SL_MAPPING_TABLE_RECORD;
 * 
 * 
 * typedef struct { IB_BITFIELD2( uint8,
 *     Reserved:   3,
 *     SL:         5 )
 * } STL_SL;
 * </pre>
 * 
 * @author jypak
 * 
 */
public class SC2SLMTRecord extends SimpleDatagram<SC2SLMTRecordBean> {

    public SC2SLMTRecord() {
        super(40);// 4+2+2+1*32
    }

    public void setLID(int lid) {
        buffer.putInt(0, lid);
    }

    public void setSC2SLMTData(byte[] data) {
        if (data.length != SAConstants.STL_MAX_SLS) {
            throw new IllegalArgumentException("Invalid data length. Expect "
                    + SAConstants.STL_MAX_SLS + ", got " + data.length);
        }

        buffer.position(8);
        for (byte val : data) {
            buffer.put(val);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public SC2SLMTRecordBean toObject() {
        buffer.clear();
        int lid = buffer.getInt();

        buffer.position(8);
        byte[] data = new byte[SAConstants.STL_MAX_SLS];
        buffer.get(data);
        SC2SLMTRecordBean bean = new SC2SLMTRecordBean(lid, data);
        return bean;
    }

}